/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.readme;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * README generator.
 */

public final class ReadMe
{
  private ReadMe()
  {

  }

  /**
   * Command-line entry point.
   *
   * @param args The arguments
   *
   * @throws Exception On errors
   */

  public static void main(final String[] args)
    throws Exception
  {
    final var document = parsePOM();
    final var projectName =
      retrieveOne(document, "/project/name").getTextContent();
    final var codacyId =
      retrieveOne(document, "/project/properties/com.codacy.id").getTextContent();

    final var nameParts = List.of(projectName.split("\\."));
    final var shortName = nameParts.get(nameParts.size() - 1);

    System.out.printf("%s\n", shortName);
    System.out.printf("===\n");
    System.out.printf("\n");
    System.out.printf(
      "[![Travis](https://img.shields.io/travis/io7m/%s.png?style=flat-square)](https://travis-ci.org/io7m/%s)\n",
      shortName,
      shortName);
    System.out.printf(
      "[![Maven Central](https://img.shields.io/maven-central/v/%s/%s.png?style=flat-square)](http://search.maven.org/#search%%7Cga%%7C1%%7Cg%%3A%%22%s%%22)\n",
      projectName,
      projectName,
      projectName);
    System.out.printf(
      "[![Maven Central (snapshot)](https://img.shields.io/nexus/s/https/oss.sonatype.org/%s/%s.svg?style=flat-square)](https://oss.sonatype.org/content/repositories/snapshots/com/io7m/%s/)\n",
      projectName,
      projectName,
      shortName);
    System.out.printf(
      "[![Codacy grade](https://img.shields.io/codacy/grade/%s.png?style=flat-square)](https://www.codacy.com/app/github_79/%s)\n",
      codacyId,
      shortName);
    System.out.printf(
      "[![Codecov](https://img.shields.io/codecov/c/github/io7m/%s.png?style=flat-square)](https://codecov.io/gh/io7m/%s)\n",
      shortName,
      shortName);

    System.out.printf("\n");
    System.out.printf("![%s](./src/site/resources/%s.jpg?raw=true)\n", shortName, shortName);
    System.out.printf("\n");

    final var extra = Paths.get("README.in");
    if (Files.isRegularFile(extra)) {
      System.out.println(new String(Files.readAllBytes(extra), StandardCharsets.UTF_8));
    }
  }

  private static List<Element> retrieve(
    final Document document,
    final String expression)
    throws Exception
  {
    final var xPath = XPathFactory.newInstance().newXPath();
    final var nodeList = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
    final var elements = new ArrayList<Element>(nodeList.getLength());
    for (var index = 0; index < nodeList.getLength(); ++index) {
      elements.add((Element) nodeList.item(index));
    }
    return List.copyOf(elements);
  }

  private static Element retrieveOne(
    final Document document,
    final String expression)
    throws Exception
  {
    final var retrieve = retrieve(document, expression);
    if (retrieve.isEmpty()) {
      throw new IllegalStateException("No elements returned for expression: " + expression);
    }
    return retrieve.get(0);
  }

  private static Document parsePOM()
    throws Exception
  {
    final var documentBuilders = DocumentBuilderFactory.newInstance();
    final var documentBuilder = documentBuilders.newDocumentBuilder();
    return documentBuilder.parse(new File("pom.xml"));
  }
}