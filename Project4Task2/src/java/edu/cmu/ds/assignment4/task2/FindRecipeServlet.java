package edu.cmu.ds.assignment4.task2;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ZEXIAN
 */
@WebServlet(urlPatterns = {"/recipe", "/dashboard"})
public class FindRecipeServlet extends HttpServlet {
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // if request for recipe, search recipe for client
        if (request.getServletPath().toString().equals("/recipe")) {
            clientOperation(request, response);
//            if want to visit dashboard, return the view
        } else if (request.getServletPath().toString().equals("/dashboard")) {
            MongoDBClient client = new MongoDBClient();
//            put attributes into request and forward the view
            request.setAttribute("delay", client.getAvgDelay());
            request.setAttribute("success", client.getCount());
            request.setAttribute("user", client.getUsersCount());
            request.setAttribute("colls", client.getCollections());
            RequestDispatcher view = request.getRequestDispatcher("dashboard.jsp");
            view.forward(request, response);
        }
    }
    /**
     * return recipe json to client
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException
     * @throws IOException 
     */
    private void clientOperation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        MongoDBClient client = new MongoDBClient();
//        record logs into client docs and save to database
        String ua = request.getHeader("User-Agent");
        long start = System.currentTimeMillis();
        Date currentDate = new Date(start);
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String startTime = df.format(currentDate);
        client.put("clientUrl", request.getRequestURL().toString());
        client.put("clientIp", request.getRemoteAddr().toString());
        client.put("requestTime", startTime);
        String food = request.getParameter("food");
        client.put("keyword", food);
        client.put("userAgent", ua);
//        get the phone model from user agent (mainly operating system)
        if (ua.toLowerCase().contains("iphone")) {
            Matcher matcher = Pattern.compile("(iPhone OS [0-9_]+)").matcher(ua);
            if (matcher.find()) {
                String model = matcher.group(0);
                client.put("model", model);
            } else {
                client.put("model", "Unkown");
            }
        } else if (ua.toLowerCase().contains("android")) {
            Matcher matcher = Pattern.compile("(Android [0-9.]+);").matcher(ua);
            if (matcher.find()) {
                String model = matcher.group(0);
                client.put("model", model);
            } else{
                client.put("model", "Unkown");
            }
        } else{
            client.put("model", "Unkown");
        }
        PrintWriter out = response.getWriter();
//        if invalid request, just send 400 and record a failed request
        if (food == null || food.length() == 0) {
            response.setStatus(400);
            client.put("success", 0);
            long end = System.currentTimeMillis();
            Date endDate = new Date(end);
            String endTime = df.format(endDate);
            client.put("responseTime", endTime);
            client.put("timeCost", end - start);
            client.put("result", "Wrong request");
            if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
                client.insertDB();
            }
            return;
        }
        ApiCall api = new ApiCall();
        String result = api.fetchData(food, client);
        client.put("success", 1);
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
//        if no result found, set success to 0
        if (result.equals("")) {
            client.put("success", 0);
            long end = System.currentTimeMillis();
            Date endDate = new Date(end);
            String endTime = df.format(endDate);
            client.put("responseTime", endTime);
            client.put("result", "No Result Found");
            client.put("timeCost", end - start);
            out.println("No Result Found");
        } else {
//            if success, record logs and forward response.
            client.put("result", result);
            long end = System.currentTimeMillis();
            Date endDate = new Date(end);
            String endTime = df.format(endDate);
            client.put("responseTime", endTime);
            client.put("timeCost", end - start);
            out.println(result);
        }
        System.out.println(ua);
//        only record logs and analytics for mobile phone (android and iphone)
        if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
            System.out.println("ererere");
           client.insertDB();
        }
    }

}
