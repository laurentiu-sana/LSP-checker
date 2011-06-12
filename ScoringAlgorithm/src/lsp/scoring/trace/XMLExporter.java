package lsp.scoring.trace;

import java.io.File;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLExporter {
	private Element root;
	private Element currentRoot;
	private Document xmldoc;
	private String filename;
	private LinkedList<Element> stack;

	public XMLExporter(String filename) {
		try {
			this.filename = filename;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			xmldoc = dbf.newDocumentBuilder().newDocument();
			stack = new LinkedList<Element>();
			root = xmldoc.createElement("LSPChecker");
			xmldoc.appendChild(root);
			currentRoot = root;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void down(String title) {
		stack.push(currentRoot);
		Element aux = xmldoc.createElement(title);
		currentRoot.appendChild(aux);
		currentRoot = aux;
	}

	public void up() {
		currentRoot = stack.pop();
	}

	public void write(String title, String key, String value) {
		Element newChild = xmldoc.createElement(title);
		currentRoot.appendChild(newChild);
		newChild.setAttribute(key, value);
	}

	public void close() {
		try {
			Source source = new DOMSource(xmldoc);
			File file = new File(filename);
			Result result = new StreamResult(file);
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
