#
#    Copyright 2025 Nacho Brito
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

IMAGE_NAME="json-schema-compiler"
WORK_FOLDER="./target"
RELEASES_FOLDER="./bin"
TODAY=$(date +"%Y-%m-%d")

#see: https://graalvm.github.io/native-build-tools/latest/maven-plugin-quickstart.html#_build_a_native_executable_by_detecting_resources_with_the_agent
echo "Running ./mvnw clean package"
./mvnw clean package

#todo: fix this: the native tests execution fails with "Try runing with '--enable-preview' error message
# mvn -Pnative -Dagent test
echo "Running ./mvnw  -Pnative -Dagent exec:exec@java-agent"
./mvnw -Pnative -Dagent exec:exec@java-agent

echo "Running ./mvnw -DskipTests=true -Pnative -Dagent package"
./mvnw -DskipTests=true -Pnative -Dagent package

if [[ $# -eq 0 ]] ; then
    echo 'No image name provided, the binary file will be kept in target folder'
    exit 1
fi

echo "Moving $WORK_FOLDER/$IMAGE_NAME to $RELEASES_FOLDER/$1/$IMAGE_NAME"
mkdir -p "$RELEASES_FOLDER/$1"
mv -f "$WORK_FOLDER/$IMAGE_NAME" "$RELEASES_FOLDER/$1/$IMAGE_NAME"

echo "Commit & push changes"
git add .
git commit -m "chore: native image for $1, generated on $TODAY"
git pull --rebase origin main
git push

echo "Creating tag '$1-$TODAY'"
git tag -a "$1-$TODAY" -m "Native image for $1, generated on $TODAY"
git push origin "$1-$TODAY" -f

echo "Moving tag $1-current"
git tag -af "$1-current" -m "Latest version of the native image for $1"
git push origin "$1-current" -f