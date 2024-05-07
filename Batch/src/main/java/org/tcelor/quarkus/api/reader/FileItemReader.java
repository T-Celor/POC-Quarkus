package org.tcelor.quarkus.api.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.quarkus.logging.Log;
import jakarta.batch.api.chunk.AbstractItemReader;


public class FileItemReader<T> extends AbstractItemReader {

	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	public static final String[] DEFAULT_COMMENT_PREFIXES = new String[] { "#" };
    
	private String path;
	
	private FileItemReaderMapper<T> mapper;

	private BufferedReader reader;

	private int lineCount = 0;

	protected String[] comments = DEFAULT_COMMENT_PREFIXES;

	private boolean noInput = false;

	private Charset encoding = DEFAULT_CHARSET;

	private int linesToSkip = 0;

	private boolean strict = true;

	public FileItemReader() {
	}

	public void setMapper(FileItemReaderMapper<T> mapper) {
		this.mapper = mapper;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public void setLinesToSkip(int linesToSkip) {
		this.linesToSkip = linesToSkip;
	}

	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}

    public void setResource(String path) {
        this.path = path;
    }
    
    @Override
	public T readItem() throws Exception {
		if (noInput) {
			return null;
		}

		String line = readLine();

		if (line == null) {
			return null;
		}
		else {
			try {
				return mapper.mapLine(line);
			}
			catch (Exception ex) {
				throw new IOException("Parsing error at line: " + lineCount + " in resource=["
						+ path + "], input=[" + line + "] : " + ex + " line : " + line + " - " + lineCount);
			}
		}
	}

	protected String readLine() throws IOException {
		if (reader == null) {
			throw new IOException("Reader must be open before it can be read.");
		}

		String line = null;

		try {
			line = this.reader.readLine();
			if (line == null) {
				return null;
			}
			lineCount++;
			while (isComment(line)) {
				line = reader.readLine();
				if (line == null) {
					return null;
				}
				lineCount++;
			}
		}
		catch (IOException e) {
			noInput = true;
			throw new IOException("Unable to read from resource: [" + path + "] : " + e + " line : " + line + " - " + lineCount);
		}
		return line;
	}

	protected boolean isComment(String line) {
		for (String prefix : comments) {
			if (line.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void close() throws Exception {
		lineCount = 0;
		if (reader != null) {
			reader.close();
		}
	}

	@Override
	public void open(Serializable checkpoint) throws Exception {
		if (mapper == null) {
			throw new RuntimeException("Mapper in FileItemReader must exist and should not be null.");
		}
		noInput = true;
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		if (inputStream == null) {
			throw new RuntimeException("Unable to find resource " + path + " on the classpath.");
		}
        reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
        if (reader == null) {
            if (strict) {
				throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode): " + path);
			}
			Log.warn("Input resource does not exist " + path);
			return;
        }
		for (int i = 0; i < linesToSkip; i++) {
			readLine();
		}
		noInput = false;
	}
}