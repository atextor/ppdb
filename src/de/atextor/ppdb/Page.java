package de.atextor.ppdb;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.NumberTool;

public class Page {
	public static void init() {
		Velocity.init();
	}

	public static StringWriter write(String templateFile, Map<String, Object> content) throws IOException {
		final Template template = Velocity.getTemplate("templates/"
				+ templateFile + ".vm");
		final VelocityContext context = new VelocityContext();
		for (String key : content.keySet()) {
			context.put(key, content.get(key));
		}
		context.put("numberTool", new NumberTool());
		final StringWriter writer = new StringWriter();
		template.merge(context, writer);
		writer.flush();
		return writer;
	}
}
