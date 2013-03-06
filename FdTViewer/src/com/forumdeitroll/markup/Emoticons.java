package com.forumdeitroll.markup;

import java.util.ArrayList;
import java.util.List;

public class Emoticons {
	private Emoticons() {}
	private static Emoticons me;
	public static Emoticons getInstance() {
		if (me == null)
			me = new Emoticons();
		return me;
	}
	
	public boolean replace(StringBuilder word) {
		boolean replaced = false;
		for (Emoticon e : tutte) {
			if (!replaced && word.indexOf(e.initialSequence) == 0 && e.sequenceStartWithSpace) {
				word.replace(0, e.initialSequence.length(), e.htmlReplacement);
				replaced = true;
			}
			if (e.sequenceStartWithSpace) {
				continue;
			}
			do {
				int p = word.indexOf(e.sequence);
				if (p == -1) break;
				word.replace(p, p + e.sequence.length(), e.htmlReplacement);
				replaced = true;
			} while (true);
		}
		return replaced;
	}
	
	public final List<Emoticon> serieClassica = new ArrayList<Emoticon>() {
		
		private void addEmo(String imgName, String sequence, String altText) {
			add(new Emoticon(imgName, sequence, altText));
		}
		
		{
			addEmo("1", " :)", "Sorride");
			addEmo("2", " :d", "A bocca aperta");
			addEmo("3", " ;)", "Occhiolino");
			addEmo("4", " :o", "Sorpresa");
			addEmo("5", " :p", "Con la lingua fuori");
			addEmo("6", " :\\", "A bocca storta");
			addEmo("7", " :@", "Arrabbiato");
			addEmo("8", " :s", "Perplesso");
			addEmo("9", " :$", "Imbarazzato");
			addEmo("10", " :(", "Triste");
			addEmo("11", ":'(", "In lacrime");
			addEmo("12", " :|", "Deluso");
			addEmo("13", " 8)", "Ficoso");
			addEmo("angelo", " o)", "Angioletto");
			addEmo("anonimo", "(anonimo)", "Anonimo");
			addEmo("diavoletto", " @^", "Indiavolato");
			addEmo("fantasmino", "(ghost)", "Fantasma");
			addEmo("geek", "(geek)", "Geek");
			addEmo("idea", "(idea)", "Idea!");
			addEmo("love", "(love)", "Innamorato");
			addEmo("loveamiga", "(amiga)", "Fan Amiga");
			addEmo("loveapple", "(apple)", "Fan Apple");
			addEmo("loveatari", "(atari)", "Fan Atari");
			addEmo("lovec64", "(c64)", "Fan Commodore64");
			addEmo("lovelinux", "(linux)", "Fan Linux");
			addEmo("lovewin", "(win)", "Fan Windows");
			addEmo("newbie", "(newbie)", "Newbie, inesperto");
			addEmo("noia3", " :-o", "Annoiato");
			addEmo("nolove", "(nolove)", "Disinnamorato");
			addEmo("pirata", " p)", "Pirata");
			addEmo("robot", "(cylon)", "Cylon");
			addEmo("rotfl", "(rotfl)", "Rotola dal ridere");
			addEmo("troll1", "(troll1)", "Troll occhiolino");
			addEmo("troll2", "(troll2)", "Troll chiacchierone");
			addEmo("troll3", "(troll3)", "Troll occhi di fuori");
			addEmo("troll4", "(troll4)", "Troll di tutti i colori");
			addEmo("troll", "(troll)", "Troll");

	}};
	
	public final List<Emoticon> serieEstesa = new ArrayList<Emoticon>() {
		
		private void addEmo(String imgName, String sequence, String altText) {
			add(new EmoticonExtended(imgName, sequence, altText));
		}
		
		{
			addEmo("keroppi", "$keroppi", "Keroppi");
			addEmo("lich", "$lich", "Licchione");
			addEmo("ranona", "$ranona", "Ranona");
			addEmo("angioletto2", "$angioletto", "Angioletto 2");
			addEmo("proott", "$proott", "Proott !!");
			addEmo("cool2", "$cool", "Ficoso 2");
			addEmo("anonimato", "$anonimato", "Anonimo Animato");
			addEmo("ghost", "$ghost", "Ghost Animato");
			addEmo("piange", "$piange", "Piagnina");
			addEmo("foco", "$foco", "Datte f&ograve;co");
			addEmo("poop", "$poop", "Evacua");
	}};
	
	public final List<Emoticon> tutte = new ArrayList<Emoticon>() {{
		addAll(serieClassica);
		addAll(serieEstesa);
	}};
}

