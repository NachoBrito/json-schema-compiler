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

package es.nachobrito.jsonschema.compiler.domain.schemareader;

import java.lang.constant.ClassDesc;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;

import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.CD_String;

public class StringFormat {
  public static ClassDesc toClassDesc(String jsonSchemaFormat) {
    if (jsonSchemaFormat == null) {
      return CD_String;
    }
    return switch (jsonSchemaFormat) {
      case "date-time" -> of(OffsetDateTime.class.getName());
      case "time" -> of(OffsetTime.class.getName());
      case "date" -> of(LocalDate.class.getName());
      case "duration" -> of(Duration.class.getName());

      case "ipv4" -> of(Inet4Address.class.getName());
      case "ipv6" -> of(Inet6Address.class.getName());

      case "uuid" -> of(UUID.class.getName());
      case "uri", "uri-reference", "iri", "iri-reference" -> of(URI.class.getName());
      // "email", "idn-email", "hostname", "idn-hostname"
      default -> CD_String;
    };
  }
}
