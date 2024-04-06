package com.millionairecodergame.millionairecoder.controller;

import com.millionairecodergame.millionairecoder.service.PlayerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/player")
public class PlayerController extends HttpServlet {

      protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve player name from the request
        String playerName = request.getParameter("playerName");


        if (playerName != null && !playerName.isEmpty()) {
            PlayerService playerService = new PlayerService();
            String playerCreated = playerService.createPlayer(playerName);

            if (playerCreated != null){
                // Player created successfully

                response.getWriter().write("Player created successfully!");
                response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                response.sendRedirect(request.getRequestURL().toString()+"/" + playerCreated);
            } else {
                // Player creation failed
                response.getWriter().write("Player already exists! Failed to create new player, please try with different name.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            }
        } else {
            // Invalid player name
            response.getWriter().write("Invalid player name. Please provide a valid name.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
        }
    }
}
