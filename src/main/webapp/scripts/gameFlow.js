
function shuffleList(ulId) {
    const ul = document.getElementById(ulId);
    if (ul !== null) {
        const liArray = Array.from(ul.getElementsByTagName('li'));
        for (let i = liArray.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [liArray[i], liArray[j]] = [liArray[j], liArray[i]]; // Swap elements
        }
        ul.innerHTML = '';
        liArray.forEach(li => ul.appendChild(li));
    }
}

function useLifeline(){
    let lifeNum = prompt("Enter the number of the lifeline you want to use:\n 1. 50-50 2. Phone a friend 3. Ask the audience");
    if(lifeNum < 1 || lifeNum > 3){
        alert("Invalid input");
        return;
    }
    const playerId = extractPlayerIdFromUrl(); // Get the player ID from the URL
    const questions = document.getElementsByClassName("Question");
    const questionId = questions[questions.length-1].getAttribute("id");
        // Construct the request body
        const requestBody = {
            lifeline: lifeNum,
            playerId: playerId,
            questionId: questionId
        };

        // Configure the fetch options
        const requestOptions = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
        };
        const lifelinesTable = document.getElementById('lifelines').getElementsByTagName("td");
        // Send the POST request to the server
        fetch('/millionairecoder_war_exploded/lifeline-manager', requestOptions)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text(); // Parse response as text
            })
            .then(responseText => {
                console.log(responseText);
                let response = JSON.parse(responseText);
                if(lifeNum === '1'){
                    let question = document.getElementById(questionId);
                    console.log(question)
                    let options = question.getElementsByTagName("input");
                    console.log(options)
                    let count = 0;
                    for(let i=0; i<options.length; i++){
                        console.log(options[[i]])
                        console.log(options[i].value === response["option1"])
                        if(options[i].value === response["option1"] || options[i].value === response["option2"]){
                            options[i].setAttribute("disabled", "true");
                            count++;
                        }
                    }
                    lifelinesTable[0].innerHTML = "Used";
                } else if(lifeNum === '2'){
                    alert("Audience Pole: \n"+ "option1: " + response["option1"]+ "%\n" + "option2: " + response["option2"]+ "%\n" + "option3: " + response["option3"]+ "%\n" + "option4: " + response["option4"]+"%");
                    lifelinesTable[1].innerHTML = "Used";
                } else if(lifeNum === '3'){
                    alert("Your friend says the answer is: " + response["friendAnswer"]);
                    lifelinesTable[2].innerHTML = "Used";
                }
            })
            .catch(error => {
                // Handle error
                console.error('There was a problem with the fetch operation:', error);
            });
}

function fetchQuestion() {

    const questions = document.getElementById('questions');
    const playerId = extractPlayerIdFromUrl(); // Extract player ID from URL
    let url = "/millionairecoder_war_exploded/fetch-question/"+playerId;
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text(); // Parse response as text
        })
        .then(response => {
            // Append response HTML to questions container
                if(questions.innerHTML)
                    questions.innerHTML = response;
                const forms = document.getElementsByTagName('form');
                for(let i=0; i<forms.length; i++){
                    const ul = forms[i].getElementsByTagName("ul")[0];
                    shuffleList(ul.getAttribute("id"));
                }
                if(response.includes("form")){
                    addButton();
                }
        })
        .catch(error => {
            // Handle error
            console.error('There was a problem with the fetch operation:', error);
        });
}
// Function to extract player ID from URL path
function extractPlayerIdFromUrl() {
    const path = window.location.pathname;
    const pathParts = path.split('/');
    return pathParts[pathParts.length - 1];
}

function submitAnswer(submitButton) {
    const forms = document.getElementsByTagName('form');
    const form = forms[forms.length - 1];
    const radioButtons = form.querySelectorAll('input[type="radio"]:checked');
    if (radioButtons.length === 0) {
        // No radio button is checked, handle the validation error
        alert('Please select an answer.');
    }else{
        const radioInputs = form.querySelectorAll('input[type="radio"]');
        radioInputs.forEach(input => {
            input.disabled = true;
        });
        submitButton.remove(); // Remove the submit button
        const playerId = extractPlayerIdFromUrl(); // Get the player ID from the URL
        const questions = document.getElementsByClassName("Question");
        const questionId = questions[questions.length-1].getAttribute("id");
        const url = "/millionairecoder_war_exploded/submit-answer"; // Endpoint to submit the answer
        // Data to be sent in the request body
        const selectedOption = form.querySelector('input[type="radio"]:checked');
        const answer = selectedOption.value;
        console.log(answer);
        const data = {
            playerId: playerId,
            userAnswer: answer,
            questionId: questionId
        };
        // Options for the fetch request
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json' // Specify JSON content type
            },
            body: JSON.stringify(data) // Convert data to JSON string
        };

        // Send the POST request
        fetch(url, options)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json(); // Parse response as JSON
            })
            .then(response => {

                alert(response.message +"\nAmount won: "  + response.amountWon);
                const amountWon = document.getElementById("amountWon");
                amountWon.innerHTML = parseInt(amountWon.innerHTML) + response.amountWon;

                if(response.isCorrect == true){
                    fetchQuestion();
                }else{
                    document.getElementById("lifelinebtn").setAttribute("disabled", "true");
                    const parentElement = selectedOption.parentNode;
                    parentElement.setAttribute("class", "msg-red")
                    const MESSAGE = document.createElement("h2"); // Create a button element
                    MESSAGE.innerHTML = "GAME OVER!"; // Set button text
                    MESSAGE.setAttribute("class", "msg-red")
                    document.getElementById("questions").appendChild(MESSAGE);
                }
                const corrAns = response.correctAnswer;
                const inputWithSpecificContent = document.querySelector('input[value="' + corrAns + '"]');
                if (inputWithSpecificContent) {
                    inputWithSpecificContent.parentNode.setAttribute("class", "msg-green");
                }
                // Handle success response
                console.log('Answer submitted successfully:', response);
                // Optionally, perform additional actions
            })
            .catch(error => {
                // Handle error
                console.error('There was a problem with the fetch operation:', error);
                // Optionally, display an error message to the user
            });
    }

}
function addButton(){
    const forms = document.getElementsByTagName('form');
    const form = forms[forms.length - 1];
    const div = document.createElement("div");
    div.setAttribute("class", "button-container");
    div.setAttribute("style", "text-align:center");
    const submitButton = document.createElement("button"); // Create a button element
    submitButton.setAttribute("type", "submit");
    submitButton.innerHTML = "Submit Answer"; // Set button text
    div.appendChild(submitButton);
    form.appendChild(div);
    form.addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent form submission

        const submitButton = form.querySelector('button[type="submit"]');
        submitAnswer(submitButton); // Call the submitAnswer function
    });
}

fetchQuestion(); // Fetch the first question when the page loads

