package com.forumdeitroll.markup;

import java.util.ArrayList;
import java.util.List;

public class Emoticons {
	// singleton
	private Emoticons() {}
	private static Emoticons me;
	public static Emoticons getInstance() {
		if (me == null)
			me = new Emoticons();
		return me;
	}
	
	private static final int MAX_EMOTICONS = 200;
	
	public int replace(StringBuilder word, int emotiCount) {
		int count = 0;
		for (Emoticon e : tutte) {
			if (emotiCount == MAX_EMOTICONS) return emotiCount;
			if (count == 0 && word.indexOf(e.initialSequence) == 0 && e.sequenceStartWithSpace) {
				word.replace(0, e.initialSequence.length(), e.htmlReplacement);
				count++;
				emotiCount++;
			} else if (count == 0 && word.indexOf(e.initialSequenceUpcase) == 0 && e.sequenceStartWithSpace) {
				word.replace(0, e.initialSequenceUpcase.length(), e.htmlReplacement);
				count++;
				emotiCount++;
			}
			if (e.sequenceStartWithSpace) {
				continue;
			}
			do {
				if (emotiCount == MAX_EMOTICONS) return emotiCount;
				int p = word.indexOf(e.sequence);
				if (p == -1) {
					p = word.indexOf(e.sequenceUpcase);
				}
				if (p == -1) {
					break;
				}
				word.replace(p, p + e.sequence.length(), e.htmlReplacement);
				count++;
				emotiCount++;
			} while (true);
		}
		return emotiCount;
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
	
	// usato per aggiornare pagina di markup
	public static void main(String[] args) throws Exception {
		int count = 0;
		for (Emoticon e : getInstance().tutte) {
			System.out.print("<li><code>");
			System.out.print(e.sequence);
			System.out.print("</code> -&gt; ");
			System.out.print(e.htmlReplacement.replace("src='", "src='http://forumdeitroll.com/"));
			System.out.println();
		}
	}
}

