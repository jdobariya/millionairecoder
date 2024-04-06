package com.millionairecodergame.millionairecoder.servlet;

import com.millionairecodergame.millionairecoder.model.Player;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import com.millionairecodergame.millionairecoder.service.DatabaseService;

@WebServlet(name = "PlayerServlet", value = "/player/*")
public class PlayerServlet extends HttpServlet {

    public void init() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatabaseService databaseService = new DatabaseService();
        String url = request.getRequestURI();
        String id = url.substring(url.lastIndexOf("/") + 1);
        Player player = databaseService.findDocumentById(id, null);
        String LIFELINE1 = "1. Fifty-Fifty";
        String LIFELINE2 = "2. Ask the Audience";
        String LIFELINE3 = "3. Phone a Friend";
        String headSection = "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <link rel=\"stylesheet\" href=\"../css/style.css\">\n" +
                "    <script src=\"../scripts/gameFlow.js\"></script>\n" +
                "    <title>Millionaire Game</title>\n" +
                "</head>";
        System.out.println(player.getPlayerName() + " " + player.getAmountWon() + " " + Arrays.toString(player.getLifelines()));
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!doctype html>"+headSection+"<body >");
        String TITLE = "Millionaire Game";
        out.println("<h1>" + TITLE + "</h1>");
        out.println("<div id='playerInfo'><div>");
        out.println("<h2>Player Information</h2>");
        out.println("<table id='score'>");
        out.println("<thead>");
        out.println("<tr><th>Name</th><th>Score</th></tr>");
        out.println("</thead>");
        out.println("<tbody>");
        out.println("<tr>");
        out.println("<td>" + player.getPlayerName() + "</td>");
        out.println("<td id='amountWon'>" + player.getAmountWon() + "</td>");
        out.println("</tr>");
        out.println("</tbody>");
        out.println("</table></div><div>");
        out.println("<h2>Lifelines</h2>");
        out.println("<button id='lifelinebtn' onclick='useLifeline()'>Use Lifeline</button>");
        out.println("<table id='lifelines'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>" + LIFELINE1 + "</th>");
        out.println("<th>" + LIFELINE2 + "</th>");
        out.println("<th>" + LIFELINE3 + "</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");
        out.println("<tr>");
        if(player.getLifelines()[0]){
            out.println("<td>" + "Used" + "</td>");
        }else{
            out.println("<td>" + "Not Used" + "</td>");
        }
        if(player.getLifelines()[1]){
            out.println("<td>" + "Used" + "</td>");
        }else{
            out.println("<td>" + "Not Used" + "</td>");
        }
        if(player.getLifelines()[2]){
            out.println("<td>" + "Used" + "</td>");
        }else{
            out.println("<td>" + "Not Used" + "</td>");
        }
        out.println("</tr>");
        out.println("</tbody>");
        out.println("</table></div>");
        out.println("</div>");
        out.println("<div id='questions'>");
        System.out.println(player.getCurrentQuestionIndex());
        if(player.getPlayedBefore()) {
            if (player.getCurrentQuestionIndex() != -1) {
                AtomicInteger questionCounter = new AtomicInteger(1);
                player.getQuestions().forEach(plQ -> {
                    if (plQ.getUserAnswer() != null) {
                        out.println("<div class='Question' id='" + plQ.getQid() + "'>");
                        out.println("<p class='question-text'>Q" + questionCounter.getAndIncrement() + ". ");
                        out.println(plQ.getQuestion());
                        out.println("</p>");
                        out.println("<form action='submitAnswer' method='post'>");
                        out.println("<ul class='options'>");
                        plQ.getIncorrectAnswers().forEach(ans -> {
                            if (ans.equals(plQ.getUserAnswer())) {
                                out.println("<li class='options'>");
                                out.println("<label class='option-label msg-red'>");
                                out.println("<input type='radio' name='option' value='" + ans + "' class='option-input' checked disabled>");
                            } else {
                                out.println("<li class='options'>");
                                out.println("<label class='option-label' >");
                                out.println("<input type='radio' name='option' value='" + ans + "' class='option-input' disabled>");
                            }
                            out.println(ans);
                            out.println("</label>");
                            out.println("</li>");
                        });
                        boolean isCorrect = plQ.getCorrectAnswer().equals(plQ.getUserAnswer());
                        out.println("<li class='options' >");
                        out.println("<label class='option-label msg-green'>");
                        out.println("<input type='radio' name='selectedOption' value='" + plQ.getCorrectAnswer() + "' class='option-input'" + (isCorrect ? "checked" : "") + " disabled>");
                        out.println(plQ.getCorrectAnswer());
                        out.println("</label>");
                        out.println("</li>");

                        out.println("</ul>");
                        out.println("</form>");
                        out.println("</div>");
                    }
                });
            }
            if(player.getCurrentQuestionIndex() != 12){
                out.println("<h2 class='msg-red'>GAME OVER!</h2>");
            }
        }
        System.out.println(player.getCurrentQuestionIndex());
        out.println("</div>");;
        out.println("</body><script src='../scripts/gameFlow.js'></script></html>");
    }


    public void destroy() {
    }
}