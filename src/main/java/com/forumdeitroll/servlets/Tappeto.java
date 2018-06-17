package com.forumdeitroll.servlets;

import net.coobird.thumbnailator.filters.Caption;
import net.coobird.thumbnailator.geometry.Coordinate;
import net.coobird.thumbnailator.tasks.io.OutputStreamImageSink;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Tappeto extends HttpServlet {

	private BufferedImage tappeto;
	private static final Font font = new Font("Dialog", Font.PLAIN, 40);

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			InputStream is = getClass().getResourceAsStream("/tappeto.jpg");
			tappeto = ImageIO.read(is);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String queryString = req.getQueryString();
		if (StringUtils.isEmpty(queryString)) {
			writeOutputImage(tappeto, resp.getOutputStream());
			return;
		}

		String[] names = StringUtils.split(queryString, ',');
		if (names.length != 2) {
			writeOutputImage(tappeto, resp.getOutputStream());
			return;
		}

		String from = names[0];
		from = StringUtils.abbreviate(from, 12);
		if (StringUtils.isEmpty(from)) {
			writeOutputImage(tappeto, resp.getOutputStream());
			return;
		}

		String to = names[1];
		to = StringUtils.abbreviate(to, 12);
		if (StringUtils.isEmpty(to)) {
			writeOutputImage(tappeto, resp.getOutputStream());
			return;
		}

		Caption fromCaption = new Caption(from, font, Color.WHITE, new Coordinate(325, 12), 5);
		Caption toCaption = new Caption(to, font, Color.WHITE, new Coordinate(300, 290), 5);

		BufferedImage outputImage = fromCaption.apply(tappeto);
		outputImage = toCaption.apply(outputImage);

    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		writeOutputImage(outputImage, bOut);
    byte[] output = bOut.toByteArray();

    resp.setHeader("Content-Type", "image/jpeg");
    resp.setHeader("Content-Length", ""+output.length);
    resp.setDateHeader("Expires", System.currentTimeMillis() + 604800000L);
    resp.getOutputStream().write(output);
	}

	private void writeOutputImage(BufferedImage outputImage, OutputStream outputStream) throws IOException {
		OutputStreamImageSink out = new OutputStreamImageSink(outputStream);
		out.setOutputFormatName("jpg");
		out.write(outputImage);
	}

}
