window.onload = function() {
  var input = document.getElementById('input');
  var openBtn = document.getElementById('open');
  var sendBtn = document.getElementById('send');
  var closeBtn = document.getElementById('close');
  var messages = document.getElementById('messages');

  var socket;

  openBtn.onclick = function(e) {
    e.preventDefault();
    if (socket !== undefined) {
      chatapp.core.output("error", "Already connected");
      return;
    }

    var uri = "ws://" + location.host + location.pathname;
    uri = uri.substring(0, uri.lastIndexOf('/'));
    socket = new WebSocket(uri);

    socket.onerror = chatapp.core.onerror;
    socket.onopen = chatapp.core.onopen;
    socket.onmessage = chatapp.core.onmessage;

    socket.onclose = function(event) {
      chatapp.core.output("closed", "Disconnected: " + event.code + " " + event.reason);
      socket = undefined;
    };
  };

  sendBtn.onclick = function(e) {
    if (socket == undefined) {
      chatapp.core.output("error", 'Not connected');
      return;
    }
    var text = document.getElementById("input").value;
    socket.send(text);
    chatapp.core.output("sent", ">>> " + text);
  };

  closeBtn.onclick = function(e) {
    if (socket == undefined) {
      chatapp.core.output('error', 'Not connected');
      return;
    }
    socket.close(1000, "Close button clicked");
  };
};
