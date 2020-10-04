const webSocketEndpoint = "ws://localhost:8080"

const connection = new WebSocket(webSocketEndpoint);
const button = document.querySelector("#send");

let activeUserId = 'SenderId1';

let users = [
    ['SenderId1', ['Messages']],
    ['SenderId2', ['Messages']],
    ['SenderId3', ['Messages']],
    ['SenderId4', ['Messages']]
];


connection.onopen = (event) => {
    console.log("WebSocket is open now.");
};

connection.onclose = (event) => {
    console.log("WebSocket is closed now.");
};

connection.onerror = (event) => {
    console.error("WebSocket error observed:", event);
};

connection.onmessage = (event) => {
  // append received message from the server to the DOM element 
  const chat = document.querySelector("#chat");

  const message = event.data;
  users[users.indexOf(message.senderId)][1] += message;

  if (activeUserId == message.senderId) {
    chat.innerHTML += `<p>${message.timestamp}: ${message.content}</p>`;
  }

  const usersDom = document.querySelector("#users");
  usersDom.innerHTML = '';    
  users.forEach( function(thisMessage) {
    //   if () # new user then add to users array and render usersdom new
      chat.innerHTML += `<p>${thisMessage.timestamp}: ${thisMessage.content}</p>`;
  } );

  
};

function changeUser(newUserId){
    activeUserId = newUserId;
    const chat = document.querySelector("#chat");
    chat.innerHTML = '';    
    users[users.indexOf(message.senderId)][1].forEach( function(thisMessage) {
        chat.innerHTML += `<p>${thisMessage.timestamp}: ${thisMessage.content}</p>`;
    } );

}

button.addEventListener("click", () => {
  const name = document.querySelector("#name");
  const newMessage = document.querySelector("#message");
  
  const message = {
    senderId: currentUser.id,
    recipientId: activeContact.id,
    senderName: currentUser.name,
    recipientName: activeContact.name,
    content: newMessage,
    timestamp: new Date(),
  };

  // Send composed message to the server
  connection.send(message);

  // clear input fields
  name.value = "";
  message.value = "";
});