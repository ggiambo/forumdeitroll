var profiler = function(callback, url) {
	var ieAcrobatVersion = function() {
		// estimate the version of Acrobat on IE using horrible horrible hacks
		if (window.ActiveXObject) {
			for ( var x = 2; x < 10; x++) {
				try {
					oAcro = eval("new ActiveXObject('PDF.PdfCtrl." + x + "');");
					if (oAcro)
						return "Adobe Acrobat version" + x + ".?";
				} catch (ex) {
				}
			}
			try {
				oAcro4 = new ActiveXObject('PDF.PdfCtrl.1');
				if (oAcro4)
					return "Adobe Acrobat version 4.?";
			} catch (ex) {
			}
			try {
				oAcro7 = new ActiveXObject('AcroPDF.PDF.1');
				if (oAcro7)
					return "Adobe Acrobat version 7.?";
			} catch (ex) {
			}
			return "";
		}
	}
	var identify_plugins = function() {
		// fetch and serialize plugins
		var plugins = "";
		// in Mozilla and in fact most non-IE browsers, this is easy
		if (navigator.plugins) {
			var np = navigator.plugins;
			var plist = new Array();
			// sorting navigator.plugins is a right royal pain
			// but it seems to be necessary because their order
			// is non-constant in some browsers
			for ( var i = 0; i < np.length; i++) {
				plist[i] = np[i].name + "; ";
				plist[i] += np[i].description + "; ";
				plist[i] += np[i].filename + ";";
				for ( var n = 0; n < np[i].length; n++) {
					plist[i] += " (" + np[i][n].description + "; " + np[i][n].type + "; " + np[i][n].suffixes + ")";
				}
				plist[i] += ". ";
			}
			plist.sort();
			for (i = 0; i < np.length; i++)
				plugins += "Plugin " + i + ": " + plist[i];
		}
		// in IE, things are much harder; we use PluginDetect to get less
		// information (only the plugins listed below & their version numbers)
		if (plugins == "") {
			var pp = new Array();
			pp[0] = "Java";
			pp[1] = "QuickTime";
			pp[2] = "DevalVR";
			pp[3] = "Shockwave";
			pp[4] = "Flash";
			pp[5] = "WindowsMediaplayer";
			pp[6] = "Silverlight";
			pp[7] = "VLC";
			var version;
			for (p in pp) {
				version = PluginDetect.getVersion(pp[p]);
				if (version)
					plugins += pp[p] + " " + version + "; ";
			}
			plugins += ieAcrobatVersion();
		}
		return plugins;
	};
	var MD5_T = new Array(0x00000000, 0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee, 0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501, 0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be, 0x6b901122,
			0xfd987193, 0xa679438e, 0x49b40821, 0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa, 0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8, 0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed, 0xa9e3e905,
			0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a, 0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c, 0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70, 0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05, 0xd9d4d039,
			0xe6db99e5, 0x1fa27cf8, 0xc4ac5665, 0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039, 0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1, 0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1, 0xf7537e82,
			0xbd3af235, 0x2ad7d2bb, 0xeb86d391);

	var MD5_round1 = new Array(new Array(0, 7, 1), new Array(1, 12, 2), new Array(2, 17, 3), new Array(3, 22, 4), new Array(4, 7, 5), new Array(5, 12, 6), new Array(6, 17, 7), new Array(7, 22, 8),
			new Array(8, 7, 9), new Array(9, 12, 10), new Array(10, 17, 11), new Array(11, 22, 12), new Array(12, 7, 13), new Array(13, 12, 14), new Array(14, 17, 15), new Array(15, 22, 16));

	var MD5_round2 = new Array(new Array(1, 5, 17), new Array(6, 9, 18), new Array(11, 14, 19), new Array(0, 20, 20), new Array(5, 5, 21), new Array(10, 9, 22), new Array(15, 14, 23), new Array(4, 20,
			24), new Array(9, 5, 25), new Array(14, 9, 26), new Array(3, 14, 27), new Array(8, 20, 28), new Array(13, 5, 29), new Array(2, 9, 30), new Array(7, 14, 31), new Array(12, 20, 32));

	var MD5_round3 = new Array(new Array(5, 4, 33), new Array(8, 11, 34), new Array(11, 16, 35), new Array(14, 23, 36), new Array(1, 4, 37), new Array(4, 11, 38), new Array(7, 16, 39), new Array(10,
			23, 40), new Array(13, 4, 41), new Array(0, 11, 42), new Array(3, 16, 43), new Array(6, 23, 44), new Array(9, 4, 45), new Array(12, 11, 46), new Array(15, 16, 47), new Array(2, 23, 48));

	var MD5_round4 = new Array(new Array(0, 6, 49), new Array(7, 10, 50), new Array(14, 15, 51), new Array(5, 21, 52), new Array(12, 6, 53), new Array(3, 10, 54), new Array(10, 15, 55), new Array(1,
			21, 56), new Array(8, 6, 57), new Array(15, 10, 58), new Array(6, 15, 59), new Array(13, 21, 60), new Array(4, 6, 61), new Array(11, 10, 62), new Array(2, 15, 63), new Array(9, 21, 64));

	var MD5_F = function(x, y, z) {
		return (x & y) | (~x & z);
	};
	var MD5_G = function(x, y, z) {
		return (x & z) | (y & ~z);
	};
	var MD5_H = function(x, y, z) {
		return x ^ y ^ z;
	};
	var MD5_I = function(x, y, z) {
		return y ^ (x | ~z);
	};

	var MD5_round = new Array(new Array(MD5_F, MD5_round1), new Array(MD5_G, MD5_round2), new Array(MD5_H, MD5_round3), new Array(MD5_I, MD5_round4));

	var MD5_pack = function(n32) {
		return String.fromCharCode(n32 & 0xff) + String.fromCharCode((n32 >>> 8) & 0xff) + String.fromCharCode((n32 >>> 16) & 0xff) + String.fromCharCode((n32 >>> 24) & 0xff);
	};

	// var MD5_unpack = function(s4) {
	// return s4.charCodeAt(0) | (s4.charCodeAt(1) << 8) | (s4.charCodeAt(2) <<
	// 16) | (s4.charCodeAt(3) << 24);
	// };

	var MD5_number = function(n) {
		while (n < 0)
			n += 4294967296;
		while (n > 4294967295)
			n -= 4294967296;
		return n;
	};

	var MD5_apply_round = function(x, s, f, abcd, r) {
		var a, b, c, d;
		var kk, ss, ii;
		var t, u;

		a = abcd[0];
		b = abcd[1];
		c = abcd[2];
		d = abcd[3];
		kk = r[0];
		ss = r[1];
		ii = r[2];

		u = f(s[b], s[c], s[d]);
		t = s[a] + u + x[kk] + MD5_T[ii];
		t = MD5_number(t);
		t = ((t << ss) | (t >>> (32 - ss)));
		t += s[b];
		s[a] = MD5_number(t);
	};

	var MD5_hash = function(data) {
		var abcd, x, state, s;
		var len, index, padLen, f, r;
		var i, j, k;
		var tmp;

		state = new Array(0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476);
		len = data.length;
		index = len & 0x3f;
		padLen = (index < 56) ? (56 - index) : (120 - index);
		if (padLen > 0) {
			data += "\x80";
			for (i = 0; i < padLen - 1; i++)
				data += "\x00";
		}
		data += MD5_pack(len * 8);
		data += MD5_pack(0);
		len += padLen + 8;
		abcd = new Array(0, 1, 2, 3);
		x = new Array(16);
		s = new Array(4);

		for (k = 0; k < len; k += 64) {
			for (i = 0, j = k; i < 16; i++, j += 4) {
				x[i] = data.charCodeAt(j) | (data.charCodeAt(j + 1) << 8) | (data.charCodeAt(j + 2) << 16) | (data.charCodeAt(j + 3) << 24);
			}
			for (i = 0; i < 4; i++)
				s[i] = state[i];
			for (i = 0; i < 4; i++) {
				f = MD5_round[i][0];
				r = MD5_round[i][1];
				for (j = 0; j < 16; j++) {
					MD5_apply_round(x, s, f, abcd, r[j]);
					tmp = abcd[0];
					abcd[0] = abcd[3];
					abcd[3] = abcd[2];
					abcd[2] = abcd[1];
					abcd[1] = tmp;
				}
			}

			for (i = 0; i < 4; i++) {
				state[i] += s[i];
				state[i] = MD5_number(state[i]);
			}
		}

		return MD5_pack(state[0]) + MD5_pack(state[1]) + MD5_pack(state[2]) + MD5_pack(state[3]);
	};

	var MD5_hexhash = function(data) {
		var i, out, c;
		var bit128;

		bit128 = MD5_hash(data);
		out = "";
		for (i = 0; i < 16; i++) {
			c = bit128.charCodeAt(i);
			out += "0123456789abcdef".charAt((c >> 4) & 0xf);
			out += "0123456789abcdef".charAt(c & 0xf);
		}
		return out;
	};
	$.ajax({
		url : url ? url : 'UserProfiler?action=prof',
		method : 'GET',
		cache : true,
		complete : function(jqXHR, textStatus) {
			if (jqXHR.status === 301) {
				profiler(callback, jqXHR.getResponseHeader('Location'));
			} else if (jqXHR.status === 304 || jqXHR.status === 200) {
				var responseData = JSON.parse(jqXHR.responseText);
				var profileData = {
					'permr' : responseData.permr,
					'etag' : responseData.etag,
					'plugins' : MD5_hexhash(identify_plugins()),
					'ua' : navigator.userAgent,
					'screenres' : screen.width + 'x' + screen.height
				};
				callback(profileData);
			}
		}
	});
};

/* non usata ?
var checkProfile = function(profileData, callback) {
	$.ajax({
		url : 'UserProfiler?action=check&jsonProfileData=' + encodeURIComponent(JSON.stringify(profileData)),
		cache : false,
		complete : function(jqXHR, textStatus) {
			if (textStatus === 'success') {
				callback(jqXHR.responseText === 'false');
			}
		}
	});
};
*/
//TODO: implementare il controllo nel codice del post del messaggio, esempio di utilizzo
//$(document).ready(function() {
//	profiler(function(profileData) {
//		checkProfile(profileData, function(check) {
//			if (check) {
//				document.body.innerHTML += '<p>ok</p>';
//			} else {
//				document.body.innerHTML += '<p>bannato</p>';
//			}
//		});
//	});
//});
