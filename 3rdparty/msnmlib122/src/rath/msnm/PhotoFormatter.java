/*
 * @(#)PhotoFormatter.java
 *
 * Copyright (c) 2001-2004, Jang-Ho Hwang
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	3. Neither the name of the Jang-Ho Hwang nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *    $Id: PhotoFormatter.java,v 1.1 2004/08/04 07:02:41 xrath Exp $
 */
package rath.msnm;

import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
/**
 *
 * @author Jang-Ho Hwang, rath@xrath.com
 * @version 1.0.000, 2004/08/03
 */
public class PhotoFormatter
{
	public static final int RESIZE_WIDTH = 96;
	public static final int RESIZE_HEIGHT = 96;	

	public BufferedImage resize( File file ) throws IOException
	{
		return resize( ImageIO.read(file) );
	}

	public BufferedImage resize( Image photo ) throws IOException
	{
		BufferedImage img = new BufferedImage( RESIZE_WIDTH, RESIZE_HEIGHT, 
			BufferedImage.TYPE_INT_RGB );
		Graphics2D g = img.createGraphics();
		
		if( photo.getWidth(null) > photo.getHeight(null) )
		{
			float gap = (photo.getWidth(null) - photo.getHeight(null)) / 2;
			float ratio = (float)RESIZE_HEIGHT / (float)photo.getHeight(null);
			float gapNew = gap * ratio;

			g.drawImage( photo, (int)-gapNew, 0, (int)(RESIZE_WIDTH+(gapNew*2)), RESIZE_HEIGHT, null );
		}
		else
		if( photo.getWidth(null) < photo.getHeight(null) )
		{
			float gap = (photo.getHeight(null) - photo.getWidth(null)) / 2;
			float ratio = (float)RESIZE_WIDTH / (float)photo.getWidth(null);
			float gapNew = gap * ratio;

			g.drawImage( photo, 0, (int)-gapNew, RESIZE_WIDTH, (int)(RESIZE_HEIGHT+(gapNew*2)), null );
		}
		else
		{
			g.drawImage( photo, 0, 0, RESIZE_WIDTH, RESIZE_HEIGHT, null );
		}
		g.dispose();

		return img;
	}

	public byte[] getPNGBytes( BufferedImage img ) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write( img, "PNG", out );
		out.close();	
		
		return out.toByteArray();
	}
}