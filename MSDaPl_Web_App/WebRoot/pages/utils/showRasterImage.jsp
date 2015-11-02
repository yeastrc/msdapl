<%@ page contentType="image/png" 
    import="javax.imageio.ImageIO,java.io.OutputStream,java.awt.image.BufferedImage"
%>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
response.addHeader("Cache-control", "no-store"); // tell proxy not to cache
response.addHeader("Cache-control", "max-age=0"); // stale right away
%>

<%



// Send back image
OutputStream os = response.getOutputStream();
ImageIO.write( (BufferedImage)request.getAttribute("image"), "png", os);
os.close();

%> 