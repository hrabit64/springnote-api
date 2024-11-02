#!/usr/bin/env python3

# ref : https://github.com/ePages-de/restdocs-api-spec/issues/109
import sys
import yaml
import json

fix_targets = ["application/json", "application/hal+json","application/json;charset=UTF-8"]

def fix_examples(res: dict):
    for key, value in res.items():
        if isinstance(value, dict):
            fix_examples(value)
        if key in fix_targets:
            for example_name, content in value["examples"].items():
                try:
                    content["value"] = json.loads(content["value"])
                except:
                    pass

with open(sys.argv[1], "r") as api_file:
    res = yaml.safe_load(api_file)
    fix_examples(res)
    print(yaml.dump(res))