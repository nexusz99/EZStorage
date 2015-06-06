package controllers;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.tika.Tika;

import Model.EZFile;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.typesafe.config.ConfigFactory;

public class LocalFileManager {
	
	public static String base = ConfigFactory.load().getString("basepath");
	
	public String store(EZFile f)
	{
		File body = f.getBody();
		Tika t = new Tika();
		String mine = null;
		File newpath = new File(base, f.getId());
		boolean r = body.renameTo(newpath);
		try {
			mine = t.detect(newpath);
			if(mine.startsWith("image"))
			{
				OutputStream os = new FileOutputStream(new File(base, f.getId()+"-thumbnail"));
				Thumbnails.of(newpath).size(100,100).toOutputStream(os);
				os.close();
			}
			else if(mine.compareTo("application/pdf")==0) {
				makaPdfThumbnail(newpath, new File(base, f.getId()+"-thumbnail"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return newpath.getPath();
	}
	
	public File getThumbNail(String path)
	{
		File body = new File(base, path);
		return body;
	}
	
	public boolean delete(String path)
	{
		File f = new File(path);
		File ft = new File(path+"-thumbnail");
		ft.delete();
		return f.delete();
	}
	public File get(String path)
	{
		File f = new File(path);
		return f;
	}
	
	private void makaPdfThumbnail(File f, File t) throws IOException
	{
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		PDFFile pdffile = new PDFFile(buf);
		
		PDFPage page = pdffile.getPage(0);
		Rectangle rect = new Rectangle(0, 0, (int)page.getBBox().getWidth(), (int)page.getBBox().getHeight());
		Image image = page.getImage(100, 100, rect, null, true, true);
		ImageIO.write((RenderedImage) image, "png", t);
	}
}
