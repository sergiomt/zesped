package com.zesped.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;
import java.awt.color.ColorSpace;
import java.awt.Transparency;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.PlanarImage;
import javax.media.jai.ParameterBlockJAI;

import com.knowgate.misc.Base64Decoder;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.zesped.Log;

/**
 * <p>Simple imaging transformations</p>
 * @author Sergio Montoro Ten
 * @version 2.1
 */

public class Picture {

  private int iImagingLibrary;

  private MediaTracker mediaTracker;

  private Image oImg;

  /**
   * Create empty Picture, use JAI imaging routines.
   */
  public Picture() {
    iImagingLibrary = USE_JAI;
    mediaTracker = null;
  }

  /**
   * Create empty Picture.
   * @param iLibraryCode Imaging library to use. Either USE_AWT or USE_JAI.
   * @throws IllegalArgumentException
   */
  public Picture(int iLibraryCode) throws IllegalArgumentException {
    setImagingLibrary(iLibraryCode);
    mediaTracker = null;
  }

  /**
   * Load Picture directly from a Java AWT abstract Image
   * @param oAWTImage java.awt.Image object
   * @param sPath Optional. Path to image file name.
   * @param iLibraryCode Imaging library to use. Either USE_AWT or USE_JAI.
   */
  public Picture(Image oAWTImage, String sPath, int iLibrary) {
    iImagingLibrary = iLibrary;
    mediaTracker = null;
    oImg = oAWTImage;
  }

  //----------------------------------------------------------------------------

  /**
   * <p>Set imaging library to use.</p>
   * On many systems it is necessary to have X-Windows started for being able to
   * use AWT imaging routines.<br>
   * @param iLibraryCode USE_AWT or USE_JAI
   * @see http://java.sun.com/products/java-media/jai/
   * @see http://java.sun.com/j2se/1.4.2/docs/api/
   */
  public void setImagingLibrary (int iLibraryCode) {
    if (iLibraryCode!=USE_AWT && iLibraryCode!=USE_JAI)
      throw new IllegalArgumentException("Imaging library code must be Image.USE_AWT or Image.USE_JAI");

    iImagingLibrary = iLibraryCode;
  } // setImagingLibrary

  //----------------------------------------------------------------------------

  /**
   * <p>Get active imaging library.</p>
   * @return USE_AWT or USE_JAI
   */
  public int getImagingLibrary() {
    return iImagingLibrary;
  } // getImagingLibrary

  //----------------------------------------------------------------------------

  /*
  public String getImageCodec() {
    String sCodec;
    String sType = getImageType();

    if (sType.equalsIgnoreCase("jpg"))
      sCodec = "jpeg";
    else if (sType.equalsIgnoreCase("tif"))
      sCodec = "tiff";
    else
      sCodec = sType;

    return sCodec;
  }

*/
  
  //----------------------------------------------------------------------------

  private int unsigned (byte by) {
    final byte MinusOne = -1;

    return (by<0) ? ((int) (by*MinusOne))+128 : (int) by;
  }

  // ----------------------------------------------------------

  private int[] dimensionsJAI(byte[] byImg, String sCodec)
    throws IOException, NullPointerException {

    int[] aRetVal = new int[2];

    RenderedImage oImg;
    ImageDecoder oDecoder;

    oDecoder = ImageCodec.createImageDecoder(sCodec, new ByteArrayInputStream(byImg), null);

    oImg = oDecoder.decodeAsRenderedImage();

    aRetVal[0] = oImg.getWidth();
    aRetVal[1] = oImg.getHeight();

    return aRetVal;
  } // dimensionsJAI

  // ----------------------------------------------------------

  /**
   * <p>Get image dimensions.</p>
   * Dimensions are stored at dm_width and dm_height properties.<br>
   * Only GIF 89a and JPG/JPEG images are supported
   * @return <b>true</b> if dimensions where successfully computed,
   * <b>false</b> if routine was unable to recognize file format.
   * @throws IOException
   * @throws ArrayIndexOutOfBoundsException
   * @throws NullPointerException
   * @throws UnsatisfiedLinkError When JAI native libraries (*_jai.so) are not
   * installed Sun JAI tries to use AWT which is slower but more compatible.
   * Some libraries of AWT are requiered. Particularly from Fedora Core 2:<br>
   * xorg-x11-devel (contains libXp.so, requiered by libawt.so),
   * fontconfig, fontconfig-devel, xorg-x11-libs, xorg-x11-libs-data, xorg-x11-Mesa-libGL
   */
  public int[] dimensions(byte[] byImg, String sCodec)
    throws IOException, FileNotFoundException, ArrayIndexOutOfBoundsException,
           UnsatisfiedLinkError, NullPointerException {

      int[] aRetVal = new int[2];
      int iFound;
      byte by;

      Log.out.debug("Begin ThumbnailCreator.dimensions("+sCodec+")");
      
      if (USE_JAI==iImagingLibrary)

    	try {
        	aRetVal = dimensionsJAI(byImg, sCodec);
        }
          catch (IOException ioe) { aRetVal = null; }

      else

    	if (sCodec.equals("gif")) {
          aRetVal[0] = (unsigned(byImg[7]))*256 + (unsigned(byImg[6]));
          aRetVal[1] = (unsigned(byImg[9]))*256 + (unsigned(byImg[8]));

        } // fi (sType==GIF)

        else if (sCodec.equals("jpeg")) {

          final int iTotal = byImg.length;

          iFound = 0;

          for (int iPos=21;
               iPos<iTotal-10;
               iPos = (2 + iPos + (unsigned(byImg[iPos + 1]) * 256 + unsigned(byImg[iPos+2]))) ) {

            by = byImg[iPos];
            if ((by>=0xC0 && by<=0xC3) || (by>=0xC5 && by<=0xC7) || (by>=0xC9 && by<=0xCB) || (by>=0xCD && by<=0xCF)) {
              iFound = iPos;
              break;
            }
          } // next (iPos)

          if (0!=iFound) {
            aRetVal[0] = (unsigned(byImg[iFound + 6]) * 256 + unsigned(byImg[iFound + 7]));
        	aRetVal[1] = (unsigned(byImg[iFound + 4]) * 256 + unsigned(byImg[iFound + 5]));
          }
          else {
        	  aRetVal=null;
          }
        }
        else
        	aRetVal=null;

      Log.out.debug("End ThumbnailCreator.dimensions() : " + aRetVal);
      
    return aRetVal;
  } // dimensions

  // ----------------------------------------------------------

  private void drawAWTImage(byte[] aImgSource, OutputStream outStr, int iThumbWidth, int iThumbHeight, float fQuality) throws IOException, InstantiationException, InterruptedException {

    Frame awtFrame;
    JPEGImageEncoder encoder;
    JPEGEncodeParam param;
    BufferedImage thumbImage;
    Graphics2D graphics2D;

    oImg = Toolkit.getDefaultToolkit().createImage(aImgSource);

    int iImageWidth = oImg.getWidth(null);
    int iImageHeight = oImg.getHeight(null);

    double thumbRatio = ((double) iThumbWidth) / ((double) iThumbHeight);
    double imageRatio = ((double) iImageWidth) / ((double) iImageHeight);

    if (thumbRatio < imageRatio)
      iImageHeight = (int)(iThumbWidth / imageRatio);
    else
      iThumbWidth = (int)(iThumbHeight * imageRatio);

    if (null==mediaTracker) {

      try {
        awtFrame = new Frame();
      } catch (Exception e) { throw new InstantiationException("Cannot instantiate java.awt.Frame " + (e.getMessage()!=null ? e.getMessage() : "")); }

      mediaTracker = new MediaTracker(awtFrame);
    } // fi (mediaTracker)

    mediaTracker.addImage(oImg, 0);
    mediaTracker.waitForID(0);

    // draw original image to thumbnail image object and
    // scale it to the new size on-the-fly
    thumbImage = new BufferedImage(iThumbWidth, iThumbHeight, BufferedImage.TYPE_INT_RGB);

    graphics2D = thumbImage.createGraphics();
    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    graphics2D.drawImage(oImg, 0, 0, iThumbWidth, iThumbHeight, null);
    graphics2D.dispose();

    encoder = JPEGCodec.createJPEGEncoder(outStr);
    param = encoder.getDefaultJPEGEncodeParam(thumbImage);

	// javax.imageio.plugins.jpeg.JPEGHuffmanTable t;
	// javax.imageio.plugins.jpeg.JPEGQTable q;
	
    fQuality = Math.max(0, Math.min(fQuality, 100));
    if (fQuality>1)
      param.setQuality(fQuality / 100.0f, false);
    else
      param.setQuality(fQuality, false);

    encoder.setJPEGEncodeParam(param);

    encoder.encode(thumbImage);

    thumbImage.flush();

    mediaTracker.removeImage(oImg, 0);

  } // drawAWTImage()


  // ----------------------------------------------------------

  private void drawJAIImage(String sCodec, InputStream imgSrc, OutputStream outStr, int iThumbWidth, int iThumbHeight, float fQuality)
    throws IOException, InterruptedException, NullPointerException, IllegalArgumentException {

    RenderedImage oRenderedImg;
    PlanarImage oPlI;
    RenderedOp oScI;
    ParameterBlock oBlk;
    ImageEncoder oImgEnc;
    ImageDecoder oDecoder;

    Log.out.debug("Begin Picture.drawJAIImage("+sCodec+","+String.valueOf(iThumbWidth)+","+String.valueOf(iThumbHeight)+")");
    
    oDecoder = com.sun.media.jai.codec.ImageCodec.createImageDecoder(sCodec, imgSrc, null);

    oRenderedImg = oDecoder.decodeAsRenderedImage();

    if (sCodec.equals("gif")) {
      // Increase color depth to 16M RGB
      try {
        javax.media.jai.ImageLayout layout = new javax.media.jai.ImageLayout();

        ColorModel cm = new ComponentColorModel (ColorSpace.getInstance(ColorSpace.CS_sRGB),
                                                 new int[] {8,8,8}, false, false,
                                                 Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        layout.setColorModel(cm);
        layout.setSampleModel(cm.createCompatibleSampleModel(oRenderedImg.getWidth(),oRenderedImg.getHeight()));
        RenderingHints hints = new RenderingHints(javax.media.jai.JAI.KEY_IMAGE_LAYOUT, layout);
        ParameterBlockJAI pb = new ParameterBlockJAI( "format" );
        pb.addSource( oRenderedImg );
        oRenderedImg = javax.media.jai.JAI.create( "format", pb, hints );
      } catch (IllegalArgumentException iae) {
      }
      // End increase color depth
    } // gif

    oPlI = PlanarImage.wrapRenderedImage(oRenderedImg);

    int iImageWidth = oPlI.getWidth();
    int iImageHeight = oPlI.getHeight();

    float thumbRatio = ((float) iThumbWidth) / ((float) iThumbHeight);

    float imageRatio = ((float) iImageWidth) / ((float) iImageHeight);

    if (thumbRatio < imageRatio)
      iThumbHeight = (int)(iThumbWidth / imageRatio);
    else
      iThumbWidth = (int)(iThumbHeight * imageRatio);

    float scaleW = ((float) iThumbWidth) / ((float) iImageWidth);

    float scaleH = ((float) iThumbHeight) / ((float) iImageHeight);

    oBlk = new ParameterBlock();

    oBlk.addSource(oPlI);

    oBlk.add(scaleW);
    oBlk.add(scaleH);
    oBlk.add(0.0f);
    oBlk.add(0.0f);
    oBlk.add(new javax.media.jai.InterpolationBilinear());

    oScI = JAI.create("scale", oBlk, null); // scale image NOW !
    		
    oImgEnc = ImageCodec.createImageEncoder( "jpeg", outStr, null );
 
    if (null==oImgEnc) {
      throw new NullPointerException("Cannot create ImageEncoder for jpeg");
    }
    else {
      oImgEnc.encode( oScI ); // write encoded data to given output stream
      oImgEnc =null;
    }

    Log.out.debug("End Picture.drawJAIImage()");

  } // drawJAIImage

  // ----------------------------------------------------------
  /**
   * <p>Resample image.</p>
   * @param sInputPath Input file path
   * @param iThumbWidth Desired width
   * @param iThumbHeight Desired height
   * @param fQuality JPG Quality [1..100]
   * @return Byte array holding the generated JPEG image
   * @throws NullPointerException
   * @throws IOException
   * @throws InterruptedException
   * @throws InstantiationException
   */
  public byte[] createThumbBitmap(byte[] aBytes, String sCodec, int iThumbWidth, int iThumbHeight, float fQuality)
    throws NullPointerException, IOException, InterruptedException, InstantiationException {

    ByteArrayOutputStream outStr = new ByteArrayOutputStream(iThumbWidth*iThumbHeight*3+1024);

    if (USE_AWT==iImagingLibrary)
      drawAWTImage (aBytes, outStr, iThumbWidth, iThumbHeight, fQuality);
    else
      drawJAIImage (sCodec, new ByteArrayInputStream(aBytes), outStr, iThumbWidth, iThumbHeight, fQuality);

    return outStr.toByteArray();
  } // createThumbBitmap()

  // ----------------------------------------------------------
  /**
   * <p>Resample image.</p>
   * @param sInputPath Input file path
   * @param sOutputPath File path where generated JPEG shall be saved.
   * @param iThumbWidth Desired width
   * @param iThumbHeight Desired height
   * @param fQuality JPG Quality [1..100]
   * @throws NullPointerException
   * @throws IOException
   * @throws InstantiationException
   * @throws InterruptedException
   * @throws InstantiationException
   */

  public void createThumbFile(byte[] aBytes, String sCodec, String sOutputPath, int iThumbWidth, int iThumbHeight, float fQuality)
    throws NullPointerException, InterruptedException, IOException, InstantiationException {
	
    FileOutputStream outStr = new FileOutputStream(sOutputPath);

    if (USE_AWT==iImagingLibrary)
      drawAWTImage (aBytes, outStr, iThumbWidth, iThumbHeight, fQuality);
    else
      drawJAIImage (sCodec, new ByteArrayInputStream(aBytes), outStr, iThumbWidth, iThumbHeight, fQuality);

    outStr.close();
    outStr=null;

  } // createThumbFile()

  // ----------------------------------------------------------

  /**
   * <p>Encode Image and write it to an OutputStream</p>
   * @param oOut OutputStream
   * @throws NullPointerException If underlying java.awt.Image object is <b>null</b>
   * @throws IOException
   * @throws InstantiationException
   * @throws InterruptedException
   */
  public void write(OutputStream oOut, String sCodec)
    throws NullPointerException,IOException,InstantiationException,InterruptedException {

    if (null==oImg) {
      throw new NullPointerException("java.awt.Image is null");
    }

    ImageEncoder oEnc = ImageCodec.createImageEncoder(sCodec, oOut, null);

    if (null==oEnc) {

      throw new InstantiationException("ImageCodec.createImageEncoder("+sCodec+")");
    }
    if (USE_JAI==iImagingLibrary) {

      RenderedImage oRImg = javax.media.jai.JAI.create("awtimage", oImg);

      if (null==oEnc) {
        throw new InstantiationException("JAI.create(awtimage, "+oImg.getClass().getName()+")");
      }

      oEnc.encode(oRImg);
    }
    else {
      int iImageWidth = oImg.getWidth(null);
      int iImageHeight = oImg.getHeight(null);

      if (null==mediaTracker) {

        Frame awtFrame = null;
        try {
          awtFrame = new Frame();
        } catch (Exception e) { throw new InstantiationException("Cannot instantiate java.awt.Frame " + (e.getMessage()!=null ? e.getMessage() : "")); }

        mediaTracker = new MediaTracker(awtFrame);
      } // fi (mediaTracker)

      mediaTracker.addImage(oImg, 0);
      mediaTracker.waitForID(0);

      BufferedImage oBImg = new BufferedImage(iImageWidth, iImageHeight, BufferedImage.TYPE_INT_RGB);

      Graphics2D graphics2D = oBImg.createGraphics();
      graphics2D.drawImage(oImg, 0, 0, iImageWidth, iImageHeight, null);
      graphics2D.dispose();

      oEnc.encode(oBImg);

      mediaTracker.removeImage(oImg,0);
    }
  }

  // ----------------------------------------------------------

  /**
   * <p>Get a transparent GIF image that can be served throught a JSP page</p>
   * Use code like
   * response.setContentType("image/gif");
   * OutputStream oOut = response.getOutputStream();
   * oOut.write(Image.blankGIF());
   * oOut.flush();
   * @since 3.0
   * @return byte[]
   */
  public static byte[] blankGIF() {
    return Base64Decoder.decodeToBytes("R0lGODlhAQABAJEAAAAAAP///////wAAACH5BAEAAAIALAAAAAABAAEAAAICVAEAOw==");
  }

  // **********************************************************
  // Public Constants

  public static final int USE_AWT = 0;
  public static final int USE_JAI = 1;

  public static final String CODEC_JPEG ="jpeg";
  public static final String CODEC_TIFF ="tiff";
  public static final String CODEC_GIF ="gif";
  
} // Image
