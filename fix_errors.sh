#!/bin/bash
# Fix Patient entity compilation

cd core-emr/src/main/java/ng/osun/his/coreemr/domain

# Verify Patient.java has all fields
if ! grep -q "deletionReason" Patient.java; then
    echo "ERROR: deletionReason field missing"
    exit 1
fi

# Check if @Data is present
if ! grep -q "@Data" Patient.java; then
    echo "ERROR: @Data annotation missing"
    exit 1
fi

# Add the field explicitly after declaring
echo "Checking Patient.java..."
grep -A 2 "deletionReason" Patient.java

echo "Done checking"
