/**
 * Sends a message to the specified user
 */
function sendMsg(user, msg){
    var stompClient = connect();
    onConnected(stompClient);

}

/**
 * Connect stomp over websocket to endpoint
 */
function connect(){
    var ws = new SockJS("http://localhost:8080/ws"); //open socket
    return Stomp.over(ws); //run stomp over socket
}

/**
 *
 */
function onConnected(client){
    console.log("connected");

    client.subscribe(
        "/user/" + currentUser.id + "/queue/messages",
        onMessageReceived
    );
}