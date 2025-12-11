/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.noren.javaapi;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author itsku
 */
public class NorenDRoutes {
    public Map<String, String> routes;
    public static String _host = "https://rama.kambala.co.in/DealerWClient/";
    // Initialize all routes,
       public NorenDRoutes(){        
        routes = new HashMap<String, String>(){{
           put("authorize", "/QuickAuth");
           put("logout", "/Logout");
           put("searchscrip", "/SearchScrip");
           put("orderbook", "/OrderBook");
           put("tradebook", "/TradeBook");
           put("placeorder", "/PlaceOrder");
           put("modifyorder", "/ModifyOrder");
           put("cancelorder", "/CancelOrder");
           put("getsecurityinfo", "/GetSecurityInfo");
           put("logout", "/Logout"); 
           put("exitorder", "/ExitSNOOrder"); 
           put("getquotes", "/GetQuotes"); 
           put("getclients", "/GetClients"); 
           put("getpositions", "/InteropPositionBook");
        }};
       }
       public String get(String key){
        return _host + routes.get(key);
    }
}
