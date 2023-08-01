import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import com.sun.net.httpserver.*;

/**
 *
 */
public class SampleServer implements HttpHandler {
	public static void main(String[] args) throws IOException {
		int port = Integer.parseInt(args[0]);
		System.out.println("Start server: http://localhost:" + port+ "/");
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new SampleServer());
		server.start();
	}

	@Override
	public void handle(HttpExchange ex)  throws IOException {
		try (OutputStream out = ex.getResponseBody()) {
			try {
				System.err.println(ex.getRemoteAddress());
				System.err.print(ex.getRequestMethod());
				System.err.print(" ");
				System.err.println(ex.getRequestURI());
				for(Entry<String, List<String>> h : ex.getRequestHeaders().entrySet()) {
					System.err.print(h.getKey() + ": ");
					System.err.println(h.getValue());
				}
				System.err.println();

				URL url = SampleServer.class.getResource(ex.getRequestURI().toString());
				if(url != null) {

					File file = new File(url.toURI());
					System.err.println(file);

					byte[] buf;
					if(file.isFile()) {
						InputStream is = SampleServer.class.getResourceAsStream(ex.getRequestURI().toString());
						buf = new byte[(int) file.length()];
						is.read(buf);
						is.close();
					}else {
						StringBuilder sb = new StringBuilder("<html><title></title><body><a href=\"../\">../</a><br>\n");
						for(File f : file.listFiles()) {
							String name = f.isDirectory()?f.getName()+"/" : f.getName();
							sb.append("<a href=\"").append(name).append("\">").append(name).append("</a><br>\n");
						}
						sb.append("</body></html>");
						buf = sb.toString().getBytes();
					}

					ex.sendResponseHeaders(200, buf.length);
					out.write(buf);
					System.err.println("200 OK\n");
				}else {
					try {
						System.err.println("404 Not Found\n");
						byte[] body = "<html><head><title>404 - Not Found</title></head><body><h1>404 - Not Found</h1></body></html>".getBytes();
						ex.sendResponseHeaders(404, body.length);
						out.write(body);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			} catch (Exception e) {
				System.err.println("500 Server Error\n");
				byte[] body = "<html><head><title>500 - Server Error</title></head><body><h1>500 - Server Error</h1></body></html>".getBytes();
				ex.sendResponseHeaders(500, body.length);
				out.write(body);
				e.printStackTrace();
			}
		} finally {
			ex.close();
		}

	}

}