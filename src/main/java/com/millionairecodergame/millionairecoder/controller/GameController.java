package com.millionairecodergame.millionairecoder.controller;



import com.millionairecodergame.millionairecoder.model.Player;

import com.millionairecodergame.millionairecoder.service.DatabaseService;
import com.millionairecodergame.millionairecoder.service.GameService;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@WebServlet("/fetch-question/*")
public class GameController extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getRequestURI();
        String playerId = url.substring(url.lastIndexOf("/") + 1);
        System.out.println("playerId: " + playerId);
        Player player = new DatabaseService().findDocumentById(playerId, null);

        if (player != null) {
            if (player.getPlayedBefore()){
                if(player.getCurrentQuestionIndex() != 12)
                    response.getWriter().write("<p>You have already played the game.</p>");
                else
                    response.getWriter().write("<h2 class='msg-green'>Congratulations! You have Won the game!</h2>");
            }else{
                GameService gameService = new GameService();
                String gameServiceResponse = gameService.questionParser(playerId);
                System.out.println("GameServiceResponse: " + gameServiceResponse);
                if (gameServiceResponse != null){
                    response.setStatus(HttpServletResponse.SC_OK); // 200 OK
                    response.setContentType("text/html");
                    response.getWriter().write(gameServiceResponse);
                } else {
                    response.getWriter().write("Failed to fetch question, please try again.");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
                }
            }
        } else {
            // Invalid player name
            response.getWriter().write("Invalid player ID. Please provide a valid player ID.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
        }

    }

}


