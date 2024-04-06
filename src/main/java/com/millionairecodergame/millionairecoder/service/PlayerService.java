package com.millionairecodergame.millionairecoder.service;
import com.millionairecodergame.millionairecoder.model.Player;
import com.millionairecodergame.millionairecoder.model.Question;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class PlayerService {

    public String createPlayer(String playerName) {
        // Check if player name already exists
        DatabaseService dbs = new DatabaseService();
        if (dbs.findDocumentById(null, playerName) != null){
            return null; // Player name already exists, creation failed
        }
        // Create a new player and add to the players map
        Player player = new Player(playerName, 0, new boolean[]{false, false, false}, new ArrayList<>());
        List<Question> questions = new ArrayList<>();
        OpenTriviaAPIService apiService = new OpenTriviaAPIService();
        String qList = null;
        Document doc = null;
        try{
            qList = apiService.fetchEasyQuestions(6);;
            if(qList != null) {
                doc = Document.parse(qList);
                doc.getList("results", Document.class).forEach(qdoc -> {
                    Question question = new Question(qdoc.getString("difficulty"), qdoc.getString("question"), qdoc.getString("correct_answer"), qdoc.getList("incorrect_answers", String.class));
                    questions.add(question);
                });
            }
            Thread.sleep(5000); // Sleep for 5 seconds
            qList = apiService.fetchMediumQuestions(4);;
            System.out.println(qList);
            if(qList != null) {
                doc = Document.parse(qList);
                doc.getList("results", Document.class).forEach(qdoc -> {
                    Question question = new Question(qdoc.getString("difficulty"), qdoc.getString("question"), qdoc.getString("correct_answer"), qdoc.getList("incorrect_answers", String.class));
                    questions.add(question);
                });
            }
            Thread.sleep(5000); //  Sleep for 5 seconds
            qList = apiService.fetchHardQuestions(3);;
            System.out.println(qList);
            if(qList != null) {
                doc = Document.parse(qList);
                doc.getList("results", Document.class).forEach(qdoc -> {
                    Question question = new Question(qdoc.getString("difficulty"), qdoc.getString("question"), qdoc.getString("correct_answer"), qdoc.getList("incorrect_answers", String.class));
                    questions.add(question);
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        player.setQuestions(questions);
        String id = dbs.insertDocument(player);
        dbs.close();
        return id; // Player created successfully
    }



}
