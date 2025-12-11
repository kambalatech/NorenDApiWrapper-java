/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.noren.javaapi;
//package okhttp3.guide;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author itsku
 */
public class NorenDApiJava {    
    String _host;
    NorenDRequests _api;
    public NorenDApiJava(String host){
        _host = host;
        _api = new NorenDRequests(host);
    }
    private String _userid;    
    private String _actid;
    private String _key;
    private String _passwordsha;
    
    public String login(String userid,String password, String twoFA, String vendor_code, String api_secret, String imei) {
        String url = _api.routes.get("authorize");
        JSONObject jsonObject = new JSONObject();
        
        String passwordsha = _api.sha256(password);        
        String appkey = userid + "|" + api_secret;
        String appkeysha = _api.sha256(appkey);
        
        jsonObject.put("source", "API");
        jsonObject.put("apkversion", "1.0.0");
        jsonObject.put("uid", userid);
        jsonObject.put("pwd", passwordsha);
        jsonObject.put("factor2", twoFA);
        jsonObject.put("vc", vendor_code);
        jsonObject.put("appkey", appkeysha);
        jsonObject.put("imei", imei);

        String response = _api.post(url, jsonObject);
        
        JSONObject jsonResp = new JSONObject(response);
        
        String stat = jsonResp.getString("stat").toString();
        if("Ok".equals(stat))
        {
            _userid = userid;
            _actid = userid;
            _key = jsonResp.getString("susertoken").toString();
        }
        
        return response;   
    }
    
    public JSONArray get_order_book(){
        String url = _api.routes.get("orderbook");
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put("uid", _userid);
        
        String response = _api.post(url, _key, jsonObject);
        System.out.println(response);
        if(response.charAt(0) == '[')
        {
            JSONArray jsonResp = new JSONArray(response);
            return jsonResp;
        }
        return null;       
    }
    public String log_out(){
        String url = _api.routes.get("logout");
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("uid", _userid);

        String response = _api.post(url, _key, jsonObject);
        
        JSONObject jsonResp = new JSONObject(response);
        
        String stat = jsonResp.getString("stat");
        if("Ok".equals(stat))
        {
        _userid = null;
        _actid = null;
        _key =null;
        _passwordsha=null;
        }
        
        return   response;   
        
    }   
    
    public JSONArray get_trade_book(){
        String url = _api.routes.get("tradebook");
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put("uid", _userid);
        jsonObject.put("actid", _actid);
        String response = _api.post(url, _key, jsonObject);
        System.out.println(response);
        if(response.charAt(0) == '[')
        {
            JSONArray jsonResp = new JSONArray(response);
            return jsonResp;
        }
        return null;         
    }
    
    public JSONObject place_order(String buy_or_sell,String product_type,String actid,
                    String exchange,String tradingsymbol,Integer quantity,Integer discloseqty,
                    String price_type,Double price,String remarks,Double trigger_price,
                    String retention, String amo,Double bookloss_price,Double bookprofit_price,Double trail_price){
        String url = _api.routes.get("placeorder");
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put("uid", _userid);
        jsonObject.put("ordersource","API");
        jsonObject.put("actid"  ,actid);
        jsonObject.put("trantype",buy_or_sell);
        jsonObject.put("prd"     ,product_type);
        jsonObject.put("exch"    ,exchange);
        jsonObject.put("tsym"    ,_api.encodeValue(tradingsymbol));
        jsonObject.put("qty"     ,Integer.toString(quantity));
        jsonObject.put("dscqty"  ,Integer.toString(discloseqty));
        jsonObject.put("prctyp"  ,price_type);
        jsonObject.put("prc"     ,Double.toString(price));
        if(null != trigger_price)
            jsonObject.put("trgprc"  ,Double.toString(trigger_price));
        if(null == retention)
            retention = "DAY";
        jsonObject.put("ret"     ,retention);
        jsonObject.put("remarks" ,remarks);
        if(null != amo)
            jsonObject.put("amo"     ,amo);
                
        String response = _api.post(url, _key, jsonObject);
        JSONObject jsonResp = new JSONObject(response);
        return jsonResp;

    }
            
    public JSONObject modify_order(String orderno,String exchange,String tradingsymbol,Integer newquantity,
                                   String newprice_type,Double newprice,Double newtrigger_price,Double bookloss_price, Double bookprofit_price , Double trail_price, String actid){
        String url = _api.routes.get("modifyorder");
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put("uid", _userid);
        jsonObject.put("ordersource","API");    
        jsonObject.put("actid"  ,actid);
        jsonObject.put("norenordno"  ,orderno);
        jsonObject.put("exch"  ,exchange);
        jsonObject.put("tsym"  ,_api.encodeValue(tradingsymbol));
        jsonObject.put("qty"  ,Integer.toString(newquantity));
        jsonObject.put("prctyp"  ,newprice_type);
        jsonObject.put("prc"  ,Double.toString(newprice));

        if (newprice_type.equals( "SL-LMT") || "SL-MKT".equals(newprice_type)){        
            if (newtrigger_price != null)
                jsonObject.put("trgprc"  ,Double.toString(newtrigger_price));
            else                
                return null;
            
        }
        //if cover order or high leverage order
        if (newtrigger_price != null)
            jsonObject.put("blprc"  ,Double.toString(bookloss_price));
        
        //trailing price
        if (trail_price != null)
            jsonObject.put("trailprc"  ,Double.toString(trail_price));
        
        //book profit of bracket order   
        if (bookprofit_price != null)
            jsonObject.put("bpprc"  ,Double.toString(bookprofit_price));
        
        String response = _api.post(url, _key, jsonObject);
        JSONObject jsonResp = new JSONObject(response);
        return jsonResp;     
     }
    public JSONObject cancel_order(String orderno){
        String url = _api.routes.get("cancelorder");
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put("uid", _userid);
        jsonObject.put("ordersource","API");
        jsonObject.put("norenordno",orderno);
        
        String response = _api.post(url, _key, jsonObject);
        JSONObject jsonResp = new JSONObject(response);
        return jsonResp; 
    }
    
    public JSONObject get_security_info(String exchange,String contract_token ){
        String url = _api.routes.get("getsecurityinfo");
        JSONObject jsonObject = new JSONObject();
         
        jsonObject.put("uid"  ,_userid);
        jsonObject.put("exch"  , exchange);
        jsonObject.put("token"  ,contract_token);
        
        String response = _api.post(url, _key, jsonObject);
        JSONObject jsonResp = new JSONObject(response);
        return jsonResp;
    }
    
    public JSONObject exit_order(String orderno,String product_type){
        String url = _api.routes.get("exitorder");
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put("uid", _userid);
        jsonObject.put("norenordno",orderno);        
        jsonObject.put("prd"     ,product_type);
        
        
        String response = _api.post(url, _key, jsonObject);
        JSONObject jsonResp = new JSONObject(response);
        return jsonResp; 
    }
    
    public JSONObject get_quotes(String exchange,String contract_token){
        String url = _api.routes.get("getquotes");
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put("uid", _userid);
        jsonObject.put("exch", exchange);      
        jsonObject.put("token"  ,contract_token);
        
        String response = _api.post(url, _key, jsonObject);
        JSONObject jsonResp = new JSONObject(response);
        
        if (!jsonResp.getString("stat").equals("Ok")) {
        return null;
        }

        return jsonResp;
     
    }
    
    public JSONObject get_clients(){
        String url = _api.routes.get("getclients");
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put("uid", _userid);
   
        String response = _api.post(url, _key, jsonObject);
        JSONObject jsonResp = new JSONObject(response);
        return jsonResp; 
    }
    
    public JSONArray get_positions(){
        String url = _api.routes.get("getpositions");
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put("uid", _userid);
        
        String response = _api.post(url, _key, jsonObject);
        System.out.println(response);
        if(response.charAt(0) == '[')
        {
            JSONArray jsonResp = new JSONArray(response);
            return jsonResp;
        }
        return null;
    }

    //public static void main(String[] args) {
    //    System.out.println("Hello and Welcom to Noren World!");
    //    NorenDApiJava api = new NorenDApiJava("http://kurma.kambala.co.in:9959/NorenWClient/");
        
    //    String response = api.login("MOBKUMAR", "Zxc@1234", "01-01-1970", "IDART_DESK", "12be8cef3b1758f5", "java-");
    //    System.out.println(response);
            
    //    JSONObject search_reply = api.search("NSE", "TCS"); 
    //    System.out.println(search_reply.toString());
        
    //    JSONObject reply = api.place_order("B","I", "NSE", "CANBK-EQ", 1, 0, "L", 220.0, "java", null, null, null, null, null, null); 
    //    System.out.println(reply.toString());
        
    //    JSONArray book  = api.get_order_book(); 
    //    System.out.println(book.toString());
        
    //    book = api.get_trade_book(); 
    //    if(book != null)
    //        System.out.println(book.toString());       
    //}   

}
