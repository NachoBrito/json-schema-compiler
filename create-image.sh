#
#    Copyright 2024 Nacho Brito
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#

#see: https://graalvm.github.io/native-build-tools/latest/maven-plugin-quickstart.html#_build_a_native_executable_by_detecting_resources_with_the_agent
mvn clean package

#todo: fix this: the native tests execution fails with "Try runing with '--enable-preview' error message
# mvn -Pnative -Dagent test
mvn -Pnative -Dagent exec:exec@java-agent
mvn -DskipTests=true -Pnative -Dagent package