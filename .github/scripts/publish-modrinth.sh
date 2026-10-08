#!/usr/bin/env bash
# Uploads the jars of one release to Modrinth, one Modrinth version per Minecraft version.
#
# Usage: publish-modrinth.sh <version> <changelog file> [jar directory, default build/libs]
# Needs MODRINTH_TOKEN (a Modrinth personal access token that can create versions).
set -euo pipefail

PROJECT_ID="QeiGauWN" # https://modrinth.com/mod/chatplus-hypixel
USER_AGENT="alikdb/chatplus (github.com/alikdb/chatplus)"

version="$1"
changelog="$2"
dir="${3:-build/libs}"
data="$(mktemp)"
trap 'rm -f "$data"' EXIT

shopt -s nullglob
jars=("$dir"/chatplus-hypixel-"$version"+*.jar)
if [ ${#jars[@]} -eq 0 ]; then
	echo "No jars for $version in $dir" >&2
	exit 1
fi

for jar in "${jars[@]}"; do
	[[ "$jar" == *-sources.jar ]] && continue

	# chatplus-hypixel-1.0.1+26.3.jar -> 26.3
	minecraft="${jar##*+}"
	minecraft="${minecraft%.jar}"

	jq -n \
		--arg name "$version for $minecraft" \
		--arg number "$version+$minecraft" \
		--arg minecraft "$minecraft" \
		--arg project "$PROJECT_ID" \
		--rawfile changelog "$changelog" \
		'{
			name: $name,
			version_number: $number,
			changelog: $changelog,
			game_versions: [$minecraft],
			loaders: ["fabric"],
			version_type: "release",
			featured: false,
			status: "listed",
			project_id: $project,
			file_parts: ["file"],
			primary_file: "file",
			dependencies: [
				{project_id: "P7dR8mSH", dependency_type: "required"},
				{project_id: "Ha28R6CL", dependency_type: "required"},
				{project_id: "mOgUt4GM", dependency_type: "optional"},
				{project_id: "Wb5oqrBJ", dependency_type: "incompatible"}
			]
		}' > "$data"

	echo "Uploading $(basename "$jar") for Minecraft $minecraft"
	curl --fail-with-body --silent --show-error --output /dev/null \
		-X POST https://api.modrinth.com/v2/version \
		-H "Authorization: $MODRINTH_TOKEN" \
		-H "User-Agent: $USER_AGENT" \
		-F "data=<$data;type=application/json" \
		-F "file=@$jar;type=application/java-archive"
done
