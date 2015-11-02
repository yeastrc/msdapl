<%@ page contentType="image/png" 
    import="java.awt.*,java.awt.image.*,
      com.sun.image.codec.jpeg.*,java.util.*,java.awt.image.renderable.*,javax.media.jai.*,
      javax.media.jai.operator.*"
%>

<%
// Send back image
ServletOutputStream sos = response.getOutputStream();
//JPEGImageEncoder encoder = 
  //JPEGCodec.createJPEGEncoder(sos);
//encoder.encode((Raster)(request.getAttribute("image")));
EncodeDescriptor.create( ((RenderedImage)(request.getAttribute("image"))), sos, "png", null, null);

%> 