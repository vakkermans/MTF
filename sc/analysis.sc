

(
	s = Server.internal;
    Server.default = s;
    s.boot;
)


(
	a.free;
	o.free;

	~processing = NetAddr("127.0.0.1", 3334);

	a = { 
		var env = EnvFollow.ar(BarkBands.ar(AudioIn.ar(1)), decaycoeff: 0.99995);
		SendReply.kr(Impulse.kr(10), "/analysis", env);
	}.play;

	o = OSCFunc({ | msg | ~processing.sendMsg(* msg) }, "/analysis");
)