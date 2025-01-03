/*
 *    Copyright 2025 Nacho Brito
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package es.nachobrito.jsonschema.compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class DataTypesTest extends CompilerTest {

  /**
   * @see <a href="https://json-schema.org/understanding-json-schema/reference/string">Json Schema
   *     string formats</a>
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws InvocationTargetException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws NoSuchMethodException
   */
  @Test
  void expectStringFormatsSupported()
      throws IOException,
          ClassNotFoundException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException,
          NoSuchMethodException,
          NoSuchFieldException {

    var json =
        """
{
  "$id": "https://example.com/person.schema.json",
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "title": "SimpleTypes",
  "type": "object",
  "properties": {
    "date-time": {
      "type": "string",
      "format": "date-time",
      "description": "Date and time together, for example, 2018-11-13T20:20:39+00:00."
    },
    "time": {
      "type": "string",
      "format": "time",
      "description": "New in draft 7 Time, for example, 20:20:39+00:00"
    },
    "date": {
      "type": "string",
      "format": "date",
      "description": "New in draft 7 Date, for example, 2018-11-13."
    },
    "duration": {
      "type": "string",
      "format": "duration",
      "description": "New in draft 2019-09 A duration as defined by the ISO 8601 ABNF for \\"duration\\". For example, P3D expresses a duration of 3 days."
    },
    "email": {
      "type": "string",
      "format": "email",
      "description": "Internet email address, see RFC 5321, section 4.1.2."
    },
    "idn-email": {
      "type": "string",
      "format": "idn-email",
      "description": "New in draft 7 The internationalized form of an Internet email address, see RFC 6531."
    },
    "hostname": {
      "type": "string",
      "format": "hostname",
      "description": "Internet host name, see RFC 1123, section 2.1."
    },
    "idn-hostname": {
      "type": "string",
      "format": "idn-hostname",
      "description": "New in draft 7 An internationalized Internet host name, see RFC5890, section 2.3.2.3."
    },
    "ipv4": {
      "type": "string",
      "format": "ipv4",
      "description": "IPv4 address, according to dotted-quad ABNF syntax as defined in RFC 2673, section 3.2."
    },
    "ipv6": {
      "type": "string",
      "format": "ipv6",
      "description": "IPv6 address, as defined in RFC 2373, section 2.2."
    },
    "uuid": {
      "type": "string",
      "format": "uuid",
      "description": "New in draft 2019-09 A Universally Unique Identifier as defined by RFC 4122. Example: 3e4666bf-d5e5-4aa7-b8ce-cefe41c7568a"
    },
    "uri": {
      "type": "string",
      "format": "uri",
      "description": "A universal resource identifier (URI), according to RFC3986."
    },
    "uri-reference": {
      "type": "string",
      "format": "uri-reference",
      "description": "New in draft 6 A URI Reference (either a URI or a relative-reference), according to RFC3986, section 4.1."
    },
    "iri": {
      "type": "string",
      "format": "iri",
      "description": "New in draft 7 The internationalized equivalent of a \\"uri\\", according to RFC3987."
    },
    "iri-reference": {
      "type": "string",
      "format": "iri-reference",
      "description": "New in draft 7 The internationalized equivalent of a \\"uri-reference\\", according to RFC3987"
    }
  }
}
""";
    var cls = compileSampleSchemaFromString(json, "SimpleTypes");
    assertNotNull(cls);

    assertEquals(OffsetDateTime.class, cls.getDeclaredField("dateTime").getType());
    assertEquals(OffsetTime.class, cls.getDeclaredField("time").getType());
    assertEquals(LocalDate.class, cls.getDeclaredField("date").getType());
    assertEquals(Duration.class, cls.getDeclaredField("duration").getType());
    assertEquals(String.class, cls.getDeclaredField("email").getType());
    assertEquals(String.class, cls.getDeclaredField("idnEmail").getType());
    assertEquals(String.class, cls.getDeclaredField("hostname").getType());
    assertEquals(String.class, cls.getDeclaredField("idnHostname").getType());
    assertEquals(Inet4Address.class, cls.getDeclaredField("ipv4").getType());
    assertEquals(Inet6Address.class, cls.getDeclaredField("ipv6").getType());
    assertEquals(UUID.class, cls.getDeclaredField("uuid").getType());
    assertEquals(URI.class, cls.getDeclaredField("uri").getType());
    assertEquals(URI.class, cls.getDeclaredField("uriReference").getType());
    assertEquals(URI.class, cls.getDeclaredField("iri").getType());
    assertEquals(URI.class, cls.getDeclaredField("iriReference").getType());
  }

  /**
   * @See <a href="https://json-schema.org/understanding-json-schema/reference/type">Json Schema
   * basic types</a>
   *
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws InvocationTargetException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws NoSuchMethodException
   */
  @Test
  void expectSimpleDataTypesSupported()
      throws IOException,
          ClassNotFoundException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException,
          NoSuchMethodException, NoSuchFieldException {

    var json =
        """
{
  "$id": "https://example.com/person.schema.json",
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "title": "SimpleTypes",
  "type": "object",
  "properties": {
    "aString": {
      "type": "string"
    },
    "anInteger": {
      "type": "integer"
    },
    "aNumber": {
      "type": "number"
    },
    "aBoolean": {
      "type": "boolean"
    }
  }
}
""";
    var cls = compileSampleSchemaFromString(json, "SimpleTypes");
    assertNotNull(cls);

    assertEquals(String.class, cls.getDeclaredField("aString").getType());
    assertEquals(Integer.class, cls.getDeclaredField("anInteger").getType());
    assertEquals(Double.class, cls.getDeclaredField("aNumber").getType());
    assertEquals(Boolean.class, cls.getDeclaredField("aBoolean").getType());

    var constructors = cls.getDeclaredConstructors();
    var instance = constructors[0].newInstance(true, 2.0, "the string", 1);

    assertNotNull(instance);
    assertEquals("the string", cls.getMethod("aString").invoke(instance));
    assertEquals(1, cls.getMethod("anInteger").invoke(instance));
    assertEquals(2.0, cls.getMethod("aNumber").invoke(instance));
    assertEquals(true, cls.getMethod("aBoolean").invoke(instance));
  }
}
