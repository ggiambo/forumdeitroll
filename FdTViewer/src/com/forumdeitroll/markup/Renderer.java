package com.forumdeitroll.markup;

import java.io.Reader;
import java.io.Writer;

import org.apache.log4j.Logger;

import com.forumdeitroll.markup.util.Code;
import com.forumdeitroll.markup.util.Color;
import com.forumdeitroll.markup.util.Images;
import com.forumdeitroll.markup.util.Line;
import com.forumdeitroll.markup.util.Section;
import com.forumdeitroll.markup.util.Spoiler;
import com.forumdeitroll.markup.util.Tags;
import com.forumdeitroll.markup.util.Url;
import com.forumdeitroll.markup.util.Word;
import com.forumdeitroll.markup.util.YouTube;


public class Renderer {
	
	//TODO: quotes, Scritto da <link>
	
	public static void render(Reader in, Writer out, RenderOptions opts) throws Exception {
		RenderIO io = new RenderIO(in, out, opts.buffersize);
		RenderState state = new RenderState();
		
		try {
		
			while (true) {
				if (!io.load())
					break;
				
				if (state.firstLine) {
					Line.firstLine(io, state, opts);
					continue;
				}
				
				if (state.codeTagOpen) {
					
					if (Tags.tags(io, state))
						continue;
					
					if (Code.codeEnd(io, state))
						continue;
					
					if (Code.brToNewline(io, state))
						continue;
					
					io.copy(1);
					io.skip(1);
					continue;
				}
				
				if (Tags.tags(io, state))
					continue;
				
				if (Code.code(io, state))
					continue;
				
				if (Images.img(io, state, opts))
					continue;
				
				if (Url.url(io, state))
					continue;
				
				if (YouTube.yt(io, state, opts))
					continue;
				
				if (Spoiler.spoiler(io, state))
					continue;
				
				if (Color.color(io, state))
					continue;
				
				if (Word.word(io, state, opts))
					continue;
				
				if (Line.line(io, state, opts))
					continue;
				
				// fallback: avanza di un carattere, non dovrebbe mai arrivare qui salvo errori nel codice
				Logger.getLogger(Renderer.class).error("fallback! -> "+new Section(io.buffer, 0, io.count));
				io.copy(1);
				io.skip(1);
			}
		
		} catch (RuntimeException e) {
			Logger.getLogger(Renderer.class).error(e);
			throw new RuntimeException(e);
		} finally {
			
			Line.cleanup(io, state, opts);
			
			Code.cleanup(io, state);
			
			Tags.cleanup(io, state);
			
			Spoiler.cleanup(io, state);
			
			Color.cleanup(io, state);
		}
		
	}
}
