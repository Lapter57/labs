package ru.spbstu.shakhmin.obfuscator;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@Component
public class XMLObfuscator implements Obfuscator {

    private static final String KEY = "XlzUUbhmC09smbIbnriH8WJaivuxOqAE";

    @NotNull
    @Override
    public String obfuscate(@NotNull final String source) {
        return processXML(source, s -> obfuscateString(s, KEY));
    }

    @NotNull
    @Override
    public String unobfuscate(@NotNull final String obfuscatedSource) {
        return processXML(obfuscatedSource, s -> unobfuscateString(s, KEY));
    }

    @NotNull
    private String processXML(@NotNull final String xml,
                              @NotNull final UnaryOperator<String> elementProcessor) {
        try {
            final var doc = getDocument(xml);
            final var root = doc.getFirstChild();
            final var queue = new ArrayDeque<Node>();
            queue.addLast(root);
            while (!queue.isEmpty()) {
                queue.addAll(processNode(queue.pollFirst(), elementProcessor));
            }
            return convertToXMLString(doc);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid xml string");
        }
    }

    @NotNull
    private Document getDocument(@NotNull final String source)
            throws ParserConfigurationException, IOException, SAXException {
        final var docFactory = DocumentBuilderFactory.newInstance();
        final var docBuilder = docFactory.newDocumentBuilder();
        final var doc = docBuilder.parse(new InputSource(new StringReader(source)));
        doc.normalizeDocument();
        return doc;
    }

    @NotNull
    private String convertToXMLString(@NotNull final Document doc) throws TransformerException {
        final var transformerFactory = TransformerFactory.newInstance();
        final var transformer = transformerFactory.newTransformer();
        final var writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    @NotNull
    private List<Node> processNode(@NotNull final Node node,
                                   @NotNull final UnaryOperator<String> elementProcessor) {
        processAttrs(node, elementProcessor);
        final var childNodes = node.getChildNodes();
        final var elementNodes = new ArrayList<Node>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final var childNode = childNodes.item(i);
            final var nodeType = childNode.getNodeType();
            if (nodeType == Node.TEXT_NODE) {
                final var text = childNode.getTextContent();
                if (!text.isBlank()) {
                    childNode.setTextContent(elementProcessor.apply(text));
                }
            } else if (nodeType == Node.ELEMENT_NODE) {
                processAttrs(childNode, elementProcessor);
                elementNodes.add(childNode);
            }
        }
        return elementNodes;
    }

    private void processAttrs(@NotNull final Node node,
                              @NotNull final UnaryOperator<String> attrProcessor) {
        final var attrs = node.getAttributes();
        if (attrs != null) {
            for (int j = 0; j < attrs.getLength(); j++) {
                final var attr = attrs.item(j);
                attr.setTextContent(attrProcessor.apply(attr.getTextContent()));
            }
        }
    }
}
