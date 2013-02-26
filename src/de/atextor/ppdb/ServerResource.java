package de.atextor.ppdb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import com.almworks.sqlite4java.SQLiteException;

@Path("/")
public class ServerResource {
	private DB db;
	
	public ServerResource() {
		this.db = DB.getInstance();
	}
	
	@GET
	@Produces("text/html")
	public String getDefault() throws IOException {
		final Map<String, Object> content = new HashMap<String, Object>();
		final List<Map<String, Object>> items = db.getAllProducts();
		content.put("list", items);
		return Page.write("productindex", content).getBuffer().toString();
	}

	@GET
	@Path("/sales")
	@Produces("text/html")
	public String getSales() throws IOException {
		final Map<String, Object> content = new HashMap<String, Object>();
		final List<Map<String, Object>> items = db.getSales();
		content.put("list", items);
		return Page.write("sales", content).getBuffer().toString();
	}

	@GET
	@Path("/product/{id}")
	@Produces("text/html")
	public String getProductPage(@PathParam("id") int id) throws IOException {
		final List<Integer> images = db.getProductImages(id);
		final Map<String, Object> content = db.getProductDescription(id);
		final List<String> features = db.getProductFeatures(id);
		final List<String> aliases = db.getProductAliases(id);
		final List<Map<String, Object>> availability = db.getProductAvailability(id);
		for (Map<String, Object> av : availability) {
			if (av.get("url").toString().length() < 40) {
				av.put("short_url", av.get("url"));
			} else {
				av.put("short_url", av.get("url").toString().substring(0, 39)
						+ "...");
			}
		}
		content.put("images", images);
		content.put("features", features);
		content.put("aliases", aliases);
		content.put("availability", availability);
		return Page.write("product", content).getBuffer().toString();
	}

	@GET
	@Path("/product/{id}/image.jpg")
	@Produces("image/jpeg")
	public InputStream getFirstImageForProduct(@PathParam("id") int id)
			throws SQLiteException, IOException {
		byte[] img = db.getProductFirstImage(id);
		if (img == null) {
			throw new WebApplicationException(404);
		}
		return new ByteArrayInputStream(img);
	}

	@GET
	@Path("/image/{id}.jpg")
	@Produces("image/jpeg")
	public InputStream getImage(@PathParam("id") int id)
			throws SQLiteException, IOException {
		byte[] img = db.getImage(id);
		if (img == null) {
			throw new WebApplicationException(404);
		}
		return new ByteArrayInputStream(img);
	}

	@GET
	@Path("/maker")
	@Produces("text/html")
	public String getMakerIndex() throws IOException {
		final List<Map<String, Object>> makers = db.getMakers();
		final Map<String, Object> content = new HashMap<String, Object>();
		content.put("makers", makers);
		return Page.write("makerindex", content).getBuffer().toString();
	}

	@GET
	@Path("/maker/{id}")
	@Produces("text/html")
	public String getMakerPage(@PathParam("id") int id) throws IOException {
		final List<Map<String, Object>> items = db.getProductsByMaker(id);
		final Map<String, Object> content = db.getMakerDescription(id);
		content.put("items", items);
		return Page.write("maker", content).getBuffer().toString();
	}

	@GET
	@Path("/seller")
	@Produces("text/html")
	public String getSellerIndex() throws IOException {
		final List<Map<String, Object>> sellers = db.getSellers();
		final Map<String, Object> content = new HashMap<String, Object>();
		content.put("sellers", sellers);
		return Page.write("sellerindex", content).getBuffer().toString();
	}

	@GET
	@Path("/seller/{id}")
	@Produces("text/html")
	public String getSellerPage(@PathParam("id") int id) throws IOException {
		final List<Map<String, Object>> items = db.getProductsBySeller(id);
		final Map<String, Object> content = db.getSellerDescription(id);
		content.put("items", items);
		return Page.write("seller", content).getBuffer().toString();
	}

}
