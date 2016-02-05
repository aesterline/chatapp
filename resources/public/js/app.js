window.onload = function() {
  var input = document.getElementById('input');
  var openBtn = document.getElementById('open');
  var sendBtn = document.getElementById('send');
  var closeBtn = document.getElementById('close');

  openBtn.onclick = function(e) {
    console.log("click");
    chatapp.core.oonclick(e);
  };

  sendBtn.onclick = chatapp.core.sonclick;

  closeBtn.onclick = chatapp.core.conclick;

};
