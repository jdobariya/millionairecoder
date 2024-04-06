package com.millionairecodergame.millionairecoder.controller;


import com.millionairecodergame.millionairecoder.model.Player;
import com.millionairecodergame.millionairecoder.service.DatabaseService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.io.*;
import java.util.Random;

import static com.millionairecodergame.millionairecoder.service.GameService.parseReqBodyData;
import static com.mongodb.client.model.Filters.eq;

interface Lifeline {
    void execute(HttpServletResponse response) throws IOException;
}

// Implement FiftyFiftyLifeline
class FiftyFiftyLifeline implements Lifeline {
    String lifeline;
    String playerId;
    String questionId;

    public FiftyFiftyLifeline(String lifeline, String playerId, String questionId){
        this.lifeline = lifeline;
        this.playerId = playerId;
        this.questionId = questionId;
    }
    @Override
    public void execute(HttpServletResponse response) throws IOException {
        DatabaseService dbs = new DatabaseService();
        Player player = dbs.getCollection().find(eq("_id",new ObjectId(playerId))).first();

        assert player != null;
        if(player.getLifelines()[0]){
            response.getWriter().println("You have already used this lifeline.");
            return;
        }
        Document incorrectAnswer = new Document();
        player.getQuestions().forEach(question -> {
            if(question.getQid().equals(questionId)){
                incorrectAnswer.put("option1", question.getIncorrectAnswers().get(0));
                incorrectAnswer.put("option2", question.getIncorrectAnswers().get(1));
            }
        });
        player.getLifelines()[0] = true;
        dbs.getCollection().updateOne(eq("_id",new ObjectId(playerId)),new Document("$set",new Document("lifelines",player.getLifelines())));
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(incorrectAnswer.toJson());
    }
}

// Implement AskTheAudienceLifeline
class AskTheAudienceLifeline implements Lifeline {
    String lifeline;
    String playerId;
    String questionId;

    public AskTheAudienceLifeline(String lifeline, String playerId, String questionId){
        this.lifeline = lifeline;
        this.playerId = playerId;
        this.questionId = questionId;
    }

    public int[] generateRandomPercentages(int numberOfPercentages, int totalSum) {
        Random random = new Random();
        int[] percentages = new int[numberOfPercentages];
        int remainingSum = totalSum;

        // Generate random percentages
        for (int i = 0; i < numberOfPercentages - 1; i++) {
            percentages[i] = random.nextInt(remainingSum);
            remainingSum -= percentages[i];
        }
        percentages[numberOfPercentages - 1] = remainingSum;

        // Shuffle the percentages array
        shuffleArray(percentages);

        return percentages;
    }

    private void shuffleArray(int[] arr) {
        Random rnd = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Swap elements at index i and index
            int temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }

    @Override
    public void execute(HttpServletResponse response) throws IOException {

        DatabaseService dbs = new DatabaseService();
        Player player = dbs.getCollection().find(eq("_id",new ObjectId(playerId))).first();

        assert player != null;
        if(player.getLifelines()[1]){
            response.getWriter().println("You have already used this lifeline.");
            return;
        }

        Document audienceResponse = new Document();
        int[] percentages = generateRandomPercentages(4, 100);
        audienceResponse.put("option1", percentages[0]);
        audienceResponse.put("option2", percentages[1]);
        audienceResponse.put("option3", percentages[2]);
        audienceResponse.put("option4", percentages[3]);

        player.getLifelines()[1] = true;
        dbs.getCollection().updateOne(eq("_id",new ObjectId(playerId)),new Document("$set",new Document("lifelines",player.getLifelines())));
        response.getWriter().println(audienceResponse.toJson());
    }
}

// Implement PhoneAFriendLifeline
class PhoneAFriendLifeline implements Lifeline {
    String lifeline;
    String playerId;
    String questionId;

    public PhoneAFriendLifeline(String lifeline, String playerId, String questionId){
        this.lifeline = lifeline;
        this.playerId = playerId;
        this.questionId = questionId;
    }
    @Override
    public void execute(HttpServletResponse response) throws IOException {
        DatabaseService dbs = new DatabaseService();
        Player player = dbs.getCollection().find(eq("_id",new ObjectId(playerId))).first();

        assert player != null;
        if(player.getLifelines()[2]){
            response.getWriter().println("You have already used this lifeline.");
            return;
        }

        Document randAns = new Document();
        String[] answers = new String[4];
        player.getQuestions().forEach(question -> {
            if(question.getQid().equals(questionId)){
                answers[0] = question.getCorrectAnswer();
                answers[1] = question.getIncorrectAnswers().get(0);
                answers[2] = question.getIncorrectAnswers().get(1);
                answers[3] = question.getIncorrectAnswers().get(2);
            }
        });
        player.getLifelines()[2] = true;
        dbs.getCollection().updateOne(eq("_id",new ObjectId(playerId)),new Document("$set",new Document("lifelines",player.getLifelines())));

        randAns.put("friendAnswer", answers[new Random().nextInt(4)]);
        response.getWriter().println(randAns.toJson());
    }
}

@WebServlet("/lifeline-manager")
public class LifelineManagerController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JSONObject jsonObject = parseReqBodyData(request);
        String questionId = jsonObject.getString("questionId");
        String playerId = jsonObject.getString("playerId");
        String lifeline = jsonObject.getString("lifeline");

        if (lifeline != null) {
            Lifeline lifelineImpl;
            switch (lifeline) {
                case "1":
                    lifelineImpl = new FiftyFiftyLifeline(lifeline,playerId,questionId);
                    break;
                case "2":
                    lifelineImpl = new AskTheAudienceLifeline(lifeline,playerId,questionId);
                    break;
                case "3":
                    lifelineImpl = new PhoneAFriendLifeline(lifeline,playerId,questionId);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("Invalid lifeline specified.");
                    return;
            }
            lifelineImpl.execute(response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("No lifeline specified.");
        }
    }
}