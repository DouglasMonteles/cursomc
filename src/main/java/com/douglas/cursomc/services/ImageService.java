package com.douglas.cursomc.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.douglas.cursomc.services.exceptions.FileException;

@Service
public class ImageService {

	public BufferedImage getJpgFromFile(MultipartFile multipartFile) {
		String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
		if (!"png".equals(ext) && !"jpg".equals(ext)) {
			throw new FileException("Somente imagens PNG e JPG são permitidas");
		}

		try {
			BufferedImage img = ImageIO.read(multipartFile.getInputStream());
			if ("png".equals(ext)) {
				img = pngToJpg(img);
			}
			return img;
		} catch (IOException e) {
			throw new FileException("Erro ao ler arquivo");
		}
	}

	public BufferedImage pngToJpg(BufferedImage img) {
		BufferedImage jpgImage = new BufferedImage(img.getWidth(), img.getHeight(), 
				BufferedImage.TYPE_INT_RGB);
		jpgImage.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
		return jpgImage;
	}
	
	public InputStream getInputStream(BufferedImage image, String ext) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, ext, os);
			return new ByteArrayInputStream(os.toByteArray());
		} catch (IOException e) {
			throw new FileException("Erro ao ler arquivo");
		}
	}
	
	public BufferedImage cropSquare(BufferedImage image) {
		int min = (image.getHeight() <= image.getWidth()) ? image.getHeight() : image.getWidth();
		return Scalr.crop(
			image,
			(image.getWidth() / 2) - (min / 2),
			(image.getHeight() / 2) - (min / 2),
			min, 
			min
		);
	}

	public BufferedImage resize(BufferedImage image, int size) {
		return Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, size);
	}
}
