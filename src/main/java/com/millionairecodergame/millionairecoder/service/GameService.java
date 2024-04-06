package com.millionairecodergame.millionairecoder.service;

import com.millionairecodergame.millionairecoder.model.Player;
import com.millionairecodergame.millionairecoder.model.Question;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.mongodb.client.model.Filters.eq;


@WebServlet("/submit-answer")
public class GameService extends HttpServlet {
    public String questionParser(String playerId) throws IOException {
        if (playerId != null) {
            DatabaseService dbs = new DatabaseService();
            Player player = dbs.findDocumentById(playerId, null);
            if (player != null) {
                Question question = player.getQuestions().get(player.getCurrentQuestionIndex()+1);
                StringBuilder htmlContent = new StringBuilder();
                htmlContent.append("<div class='Question' id='"+question.getQid()+"'>");
                htmlContent.append("<p class='question-text'>Q"+(player.getCurrentQuestionIndex() + 1)+".");
                htmlContent.append(question.getQuestion());
                htmlContent.append("</p>");
                htmlContent.append("<form action='submitAnswer' method='post' id='form-q-"+(player.getCurrentQuestionIndex()+1)+"'>");
                htmlContent.append("<ul class='options' id='ans-list-"+(player.getCurrentQuestionIndex()+1)+"'>");
                AtomicInteger optionCounter = new AtomicInteger(1);
                question.getIncorrectAnswers().forEach(ans -> {
                    htmlContent.append("<li class='options'>");
                    htmlContent.append("<label class='option-label' >");
                    htmlContent.append("<input type='radio' name='option"+optionCounter+"' value='"+ans+"' class='option-input'>");
                    htmlContent.append(ans);
                    htmlContent.append("</label>");
                    htmlContent.append("</li>");
                });
                htmlContent.append("<li class='options'>");
                htmlContent.append("<label class='option-label'>");
                htmlContent.append("<input type='radio' name='option"+optionCounter+"' value="+question.getCorrectAnswer()+" class='option-input'>");
                htmlContent.append(question.getCorrectAnswer());
                htmlContent.append("</label>");
                htmlContent.append("</li>");
                htmlContent.append("</ul>");
                htmlContent.append("</form>");
                htmlContent.append("</div>");
                dbs.close();
                return htmlContent.toString();
            }
        }
        return null;
    }

    public Player getPlayer(String playerId) {
        DatabaseService dbs = new DatabaseService();
        Player player = dbs.findDocumentById(playerId, null);
        dbs.close();
        return player;
    }

    public boolean validateAnswer(String playerId, String questionId, String userAnswer) {
        DatabaseService dbs = new DatabaseService();
        System.out.println("Player ID: " + playerId);
        System.out.println("Question ID: " + questionId);
        System.out.println("User Answer: " + userAnswer);
        Player player = dbs.getCollection().find(eq("_id", new ObjectId(playerId))).first();
        AtomicReference<Boolean> isCorrect = new AtomicReference<>(false);
        if (player != null) {
            player.getQuestions().forEach(question -> {
                if (question.getQid().equals(questionId)) {
                    question.setUserAnswer(userAnswer);
                    player.setCurrentQuestionIndex(player.getCurrentQuestionIndex()+1);
                    if(question.getCorrectAnswer().equals(userAnswer)){

                        player.setAmountWon((player.getAmountWon()+10000));
                        dbs.getCollection().replaceOne(eq("_id", new ObjectId(playerId)), player);
                        isCorrect.set(true);
                    }else{

                        if(player.getCurrentQuestionIndex() > 5 && player.getCurrentQuestionIndex() <= 9){
                            player.setAmountWon((player.getAmountWon()-(10000*(player.getCurrentQuestionIndex()-4))));
                            dbs.getCollection().replaceOne(eq("_id", new ObjectId(playerId)), player);
                            isCorrect.set(false);
                        }else if(player.getCurrentQuestionIndex() <= 5){
                            player.setAmountWon(0);
                            dbs.getCollection().replaceOne(eq("_id", new ObjectId(playerId)), player);
                            isCorrect.set(false);
                        }
                    }
                }
            });
        }
        dbs.close();
        return isCorrect.get();
    }

    public static JSONObject parseReqBodyData(HttpServletRequest request) throws IOException{
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);

            }
        }
        return new JSONObject(requestBody.toString());
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve question ID and user's answer from the request
        // Read data from the request body
        JSONObject jsonObject = parseReqBodyData(request);
        String questionId = jsonObject.getString("questionId");
        String playerId = jsonObject.getString("playerId");
        String userAnswer = jsonObject.getString("userAnswer");

        boolean isCorrect = this.validateAnswer(playerId, questionId, userAnswer);
        DatabaseService dbs = new DatabaseService();
        Document doc = new Document();
        doc.append("correctAnswer", this.getPlayer(playerId).getQuestions().get(this.getPlayer(playerId).getCurrentQuestionIndex()).getCorrectAnswer());
        if (isCorrect) {
            // Send success response if answer is correct
            Player player = dbs.findDocumentById(playerId, null);
            if(player.getCurrentQuestionIndex() == 12){

                dbs.getCollection().updateOne(eq("_id", new ObjectId(playerId)), new Document("$set", new Document("playedBefore", true)));
                dbs.close();
            }

            doc.append("isCorrect", true);
            doc.append("message", "Correct answer!");
            doc.append("amountWon", this.getPlayer(playerId).getAmountWon());
            response.getWriter().write(doc.toJson());
            response.setStatus(HttpServletResponse.SC_OK); // 200 OK
        } else {
            // Send failure response if answer is incorrect
            dbs.getCollection().updateOne(eq("_id", new ObjectId(playerId)), new Document("$set", new Document("playedBefore", true)));
            dbs.close();

            doc.append("isCorrect", false);
            doc.append("message", "Incorrect answer! Game over!");
            doc.append("amountWon", this.getPlayer(playerId).getAmountWon());

            response.getWriter().write(doc.toJson());
            response.setStatus(HttpServletResponse.SC_OK); // 200 OK
        }

    }
}
