<%@ page contentType="image/jpeg" 
    import="java.awt.*,java.awt.image.*,
      com.sun.image.codec.jpeg.*,java.util.*"
%>

<%



// Send back image
ServletOutputStream sos = response.getOutputStream();
JPEGImageEncoder encoder = 
  JPEGCodec.createJPEGEncoder(sos);
encoder.encode((BufferedImage)(request.getAttribute("image")));

%> 