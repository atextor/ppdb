package de.atextor.ppdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;
import com.almworks.sqlite4java.SQLiteStatement;

public class DB {
	private static DB instance = null;
	
	public static DB getInstance() {
		if (instance == null) {
			instance = new DB();
		}
		return instance;
	}
	
	protected SQLiteQueue queue = null;

	public DB() {
		Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF);
		queue = new SQLiteQueue(Server.sqliteFile);
		queue.start();
	}

	protected static interface Extractor<T> {
		public T get(SQLiteStatement statement) throws SQLiteException;
	}

	@SuppressWarnings("unchecked")
	protected <T> T getResult(final String query, final Extractor<T> e) {
		return (T) queue.execute(new SQLiteJob<Object>() {
			protected Object job(SQLiteConnection conn) {
				SQLiteStatement sql = null;
				try {
					sql = conn.prepare(query);
					return e.get(sql);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				} finally {
					if (sql != null) {
						sql.dispose();
					}
				}
			}
		}).complete();
	}

	public List<Map<String, Object>> getSales() {
		return getResult(
				"SELECT product.product_id, product.name, maker.url, maker.description, maker.maker_id, seller.seller_id, seller.description, product_available.url, product_available.price "
						+ "FROM product, maker, product_available, seller "
						+ "WHERE product.maker_id = maker.maker_id AND product_available.product_id = product.product_id AND product_available.seller_id = seller.seller_id AND product_available.url IS NOT NULL "
						+ "ORDER BY product_available.price, product.name",
				new Extractor<List<Map<String, Object>>>() {
					public List<Map<String, Object>> get(SQLiteStatement sql)
							throws SQLiteException {
						final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
						while (sql.step()) {
							final Map<String, Object> item = new HashMap<String, Object>();
							item.put("product_id", sql.columnInt(0));
							item.put("product_name", sql.columnString(1));
							item.put("maker_url", sql.columnString(2));
							item.put("maker_name", sql.columnString(3));
							item.put("maker_id", sql.columnInt(4));
							item.put("seller_id", sql.columnInt(5));
							item.put("seller_name", sql.columnString(6));
							item.put("sale_url", sql.columnString(7));
							item.put("sale_price", sql.columnDouble(8));
							result.add(item);
						}
						return result;
					}
				});
	}

	public List<Map<String, Object>> getAllProducts() {
		return getResult(
				"SELECT product.product_id, product.name, maker.url, maker.description, maker.maker_id FROM product, maker "
						+ "WHERE product.maker_id = maker.maker_id ORDER BY maker.maker_id, product.name",
				new Extractor<List<Map<String, Object>>>() {
					public List<Map<String, Object>> get(SQLiteStatement sql)
							throws SQLiteException {
						final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
						while (sql.step()) {
							final Map<String, Object> item = new HashMap<String, Object>();
							item.put("product_id", sql.columnInt(0));
							item.put("product_name", sql.columnString(1));
							item.put("url", sql.columnString(2));
							item.put("maker_name", sql.columnString(3));
							item.put("maker_id", sql.columnInt(4));
							result.add(item);
						}
						return result;
					}
				});
	}

	public List<Map<String, Object>> getProductsByMaker(final int id) {
		return getResult(
				"SELECT product.product_id, product.name, maker.url, maker.description FROM product, maker "
						+ "WHERE product.maker_id = maker.maker_id AND maker.maker_id = ? ORDER BY maker.maker_id, product.name",
				new Extractor<List<Map<String, Object>>>() {
					public List<Map<String, Object>> get(SQLiteStatement sql)
							throws SQLiteException {
						final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
						sql.bind(1, id);
						while (sql.step()) {
							final Map<String, Object> item = new HashMap<String, Object>();
							item.put("product_id", sql.columnInt(0));
							item.put("product_name", sql.columnString(1));
							item.put("url", sql.columnString(2));
							item.put("maker_name", sql.columnString(3));
							result.add(item);
						}
						return result;
					}
				});
	}

	public List<Map<String, Object>> getProductsBySeller(final int id) {
		return getResult(
				"SELECT product.product_id, product.name, seller.url, seller.description, maker.maker_id, maker.description "
						+ "FROM product, seller, product_available, maker WHERE product.product_id = product_available.product_id AND "
						+ "seller.seller_id = product_available.seller_id AND product.maker_id = maker.maker_id AND "
						+ "seller.seller_id = ? ORDER BY seller.seller_id, product.name",
				new Extractor<List<Map<String, Object>>>() {
					public List<Map<String, Object>> get(SQLiteStatement sql)
							throws SQLiteException {
						final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
						sql.bind(1, id);
						while (sql.step()) {
							final Map<String, Object> item = new HashMap<String, Object>();
							item.put("product_id", sql.columnInt(0));
							item.put("product_name", sql.columnString(1));
							item.put("url", sql.columnString(2));
							item.put("seller_name", sql.columnString(3));
							item.put("maker_id", sql.columnInt(4));
							item.put("maker_name", sql.columnString(5));
							result.add(item);
						}
						return result;
					}
				});
	}

	public Map<String, Object> getProductDescription(final int id) {
		return getResult(
				"SELECT product.product_id, product.name, maker.url, maker.description, maker.maker_id FROM product, maker "
						+ "WHERE product.maker_id = maker.maker_id AND product.product_id = ? ORDER BY product.product_id",
				new Extractor<Map<String, Object>>() {
					public Map<String, Object> get(SQLiteStatement sql)
							throws SQLiteException {
						sql.bind(1, id);
						sql.step();
						final Map<String, Object> result = new HashMap<String, Object>();
						result.put("product_id", sql.columnInt(0));
						result.put("product_name", sql.columnString(1));
						result.put("url", sql.columnString(2));
						result.put("maker_name", sql.columnString(3));
						result.put("maker_id", sql.columnInt(4));
						return result;
					}
				});
	}

	public List<Integer> getProductImages(final int id) {
		return getResult(
				"SELECT product_image_id FROM product_image WHERE product_id = ?",
				new Extractor<List<Integer>>() {
					public List<Integer> get(SQLiteStatement sql)
							throws SQLiteException {
						sql.bind(1, id);
						final List<Integer> result = new ArrayList<Integer>();
						while (sql.step()) {
							result.add(sql.columnInt(0));
						}
						return result;
					}
				});
	}

	public List<String> getProductFeatures(final int id) {
		return getResult(
				"SELECT feature.name FROM feature, product_feature WHERE "
						+ "product_feature.feature_id = feature.feature_id AND product_feature.product_id = ?",
				new Extractor<List<String>>() {
					public List<String> get(SQLiteStatement sql)
							throws SQLiteException {
						sql.bind(1, id);
						final List<String> result = new ArrayList<String>();
						while (sql.step()) {
							result.add(sql.columnString(0));
						}
						return result;
					}
				});
	}

	public List<String> getProductAliases(final int id) {
		return getResult(
				"SELECT alias.name FROM alias WHERE alias.product_id = ?",
				new Extractor<List<String>>() {
					public List<String> get(SQLiteStatement sql)
							throws SQLiteException {
						sql.bind(1, id);
						final List<String> result = new ArrayList<String>();
						while (sql.step()) {
							result.add(sql.columnString(0));
						}
						return result;
					}
				});
	}

	public List<Map<String, Object>> getProductAvailability(final int id) {
		return getResult(
				"SELECT seller.seller_id, seller.description, seller.url, "
						+ "product_available.price, product_available.url FROM product_available, seller WHERE "
						+ "product_available.seller_id = seller.seller_id "
						+ "AND product_available.product_id = ?",
				new Extractor<List<Map<String, Object>>>() {
					public List<Map<String, Object>> get(SQLiteStatement sql)
							throws SQLiteException {
						sql.bind(1, id);
						final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
						while (sql.step()) {
							final Map<String, Object> item = new HashMap<String, Object>();
							item.put("seller_id", sql.columnInt(0));
							item.put("seller_name", sql.columnString(1));
							item.put("seller_url", sql.columnString(2));
							item.put("price", sql.columnDouble(3));
							item.put("url", sql.columnString(4));
							result.add(item);
						}
						return result;
					}
				});
	}

	public byte[] getImage(final int id) {
		return getResult(
				"SELECT image FROM product_image WHERE product_image_id = ?",
				new Extractor<byte[]>() {
					public byte[] get(SQLiteStatement sql)
							throws SQLiteException {
						sql.bind(1, id);
						return sql.step() ? sql.columnBlob(0) : null;
					}
				});
	}

	public byte[] getProductFirstImage(final int id) {
		return getResult(
				"SELECT product_image.image FROM product_image WHERE product_image.product_id = ? "
						+ "ORDER BY product_image.product_image_id LIMIT 1",
				new Extractor<byte[]>() {
					public byte[] get(SQLiteStatement sql)
							throws SQLiteException {
						sql.bind(1, id);
						return sql.step() ? sql.columnBlob(0) : null;
					}
				});
	}

	public List<Map<String, Object>> getMakers() {
		return getResult("SELECT maker_id, description FROM maker",
				new Extractor<List<Map<String, Object>>>() {
					public List<Map<String, Object>> get(SQLiteStatement sql)
							throws SQLiteException {
						final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
						while (sql.step()) {
							final Map<String, Object> item = new HashMap<String, Object>();
							item.put("maker_id", sql.columnInt(0));
							item.put("maker_name", sql.columnString(1));
							result.add(item);
						}
						return result;
					}
				});
	}

	public Map<String, Object> getMakerDescription(final int id) {
		return getResult(
				"SELECT maker_id, description, url FROM maker WHERE maker_id = ?",
				new Extractor<Map<String, Object>>() {
					public Map<String, Object> get(SQLiteStatement sql)
							throws SQLiteException {
						sql.bind(1, id);
						sql.step();
						final Map<String, Object> result = new HashMap<String, Object>();
						result.put("maker_id", sql.columnInt(0));
						result.put("maker_name", sql.columnString(1));
						result.put("url", sql.columnString(2));
						return result;
					}
				});
	}

	public List<Map<String, Object>> getSellers() {
		return getResult("SELECT seller_id, description FROM seller",
				new Extractor<List<Map<String, Object>>>() {
					public List<Map<String, Object>> get(SQLiteStatement sql)
							throws SQLiteException {
						final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
						while (sql.step()) {
							final Map<String, Object> item = new HashMap<String, Object>();
							item.put("seller_id", sql.columnInt(0));
							item.put("seller_name", sql.columnString(1));
							result.add(item);
						}
						return result;
					}
				});
	}

	public Map<String, Object> getSellerDescription(final int id) {
		return getResult(
				"SELECT seller_id, description, url FROM seller WHERE seller_id = ?",
				new Extractor<Map<String, Object>>() {
					public Map<String, Object> get(SQLiteStatement sql)
							throws SQLiteException {
						sql.bind(1, id);
						sql.step();
						final Map<String, Object> result = new HashMap<String, Object>();
						result.put("seller_id", sql.columnInt(0));
						result.put("seller_name", sql.columnString(1));
						result.put("url", sql.columnString(2));
						return result;
					}
				});
	}

}
