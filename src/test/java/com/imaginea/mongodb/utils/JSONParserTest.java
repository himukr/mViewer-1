package com.imaginea.mongodb.utils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Test all the query parser logic
 *
 * @author himanshuk
 * @since 23/6/15.
 */
public class JSONParserTest {

    public static String queryWithOneKeyAndStringValue,
            queryWithKeyValueAsObject,
            queryWithKeyAndRegexValue,
            queryWithRegexOperatorKeyAndRegexObjValue,
            queryWithArrayValue,
            complexQueryArrayOfRegexObjectWithINkey,
            complexQueryArrayOfRegexObjectWithORKey;

    @BeforeClass
    public static void setUpQueries() {
        queryWithOneKeyAndStringValue = "{\n  name     :    \"basic query\"}";
        queryWithKeyValueAsObject = "{name:\"query Contain object\" ,  age:{$gt:35,$lt:40}}";
        queryWithArrayValue = "{name:{$in:[\"value1\",\"value2\",\"value3\"]}}";

        queryWithKeyAndRegexValue = "{name:/Ba s.*/i}";
        queryWithRegexOperatorKeyAndRegexObjValue = "{name:{$regex:/Ba s.*/,$options:\"im\"}}";

        complexQueryArrayOfRegexObjectWithINkey = "{name:{$in:[   /RegEx1.*/i , /regex2.*/m  ]}}";
        complexQueryArrayOfRegexObjectWithORKey = "{$or:[{name:\n/RegEx1\n.*/i},{name:/regex2\n.*/m     }]}";
    }

    /**
     * query parser test with one Key and one String value
     */
    @Test
    public void parseStringTest() {
        JSONParser jsonParser = new JSONParser(queryWithOneKeyAndStringValue);
        try {
            BasicDBObject basicDBObject = (BasicDBObject) jsonParser.parse();
            String parsedString = (String) basicDBObject.get("name");
            assertEquals("basic query", parsedString);
        } catch (Exception e) {
            fail("parser failed to parse");
        }
    }

    /**
     *query parser test with one Key and BSON object in value
     */
    @Test
    public void parseObjectTest() {
        JSONParser jsonParser = new JSONParser(queryWithKeyValueAsObject);
        BasicDBObject basicDBObject = (BasicDBObject) jsonParser.parse();
        BasicDBObject parsedAgeObject = (BasicDBObject) basicDBObject.get("age");

        int parsedAgeKeyGT = (Integer) parsedAgeObject.get("$gt");
        int parsedAgeKeyLT = (Integer) parsedAgeObject.get("$lt");

        assertEquals(35, parsedAgeKeyGT);
        assertEquals(40, parsedAgeKeyLT);
    }

    /**
     * query parser test with one key and regex Object in value.
     * regex obj is in format /<regex string>/
     */
    @Test
    public void parseRegexPatternObjTest() {
        JSONParser jsonParser = new JSONParser(queryWithKeyAndRegexValue);
        BasicDBObject basicDBObject = (BasicDBObject) jsonParser.parse();
        Pattern parsedRegexPattern = (Pattern) basicDBObject.get("name");

        assertEquals("Ba s.*", parsedRegexPattern.pattern());
        assertEquals(2, parsedRegexPattern.flags());
    }

    /**
     * query parser test with key is regex operator i.e.$regex and value of this regex operator is regex obj of
     * format /<regex string>/
     */
    @Test
    public void parseRegexObjInsideRegexOperatorTest() {
        JSONParser jsonParser = new JSONParser(queryWithRegexOperatorKeyAndRegexObjValue);
        BasicDBObject basicDBObject = (BasicDBObject) jsonParser.parse();
        Pattern parsedRegexPattern = (Pattern) basicDBObject.get("name");

        assertEquals("Ba s.*", parsedRegexPattern.pattern());
        assertEquals(10, parsedRegexPattern.flags());
    }

    /**
     * query parser test for parsing array values
     */
    @Test
    public void parseArrayTest() {
        JSONParser jsonParser = new JSONParser(queryWithArrayValue);
        BasicDBObject basicDBObject = (BasicDBObject) jsonParser.parse();

        BasicDBList parsedArrayListValues = (BasicDBList) (((BasicDBObject) basicDBObject.get("name")).get("$in"));

        assertEquals("value1", parsedArrayListValues.get(0));
        assertEquals("value2", parsedArrayListValues.get(1));
        assertEquals("value3", parsedArrayListValues.get(2));
    }

    /**
     * query parser test for parsing complex query containing "in" operator i.e.$in as key and the value of in operator are array of
     * regex obj of format /<regex string>/
     */
    @Test
    public void parseRegexObjArrayWithINOperator() {
        JSONParser jsonParser = new JSONParser(complexQueryArrayOfRegexObjectWithINkey);
        BasicDBObject basicDBObject = (BasicDBObject) jsonParser.parse();

        BasicDBList parsedArrayListValues = (BasicDBList) (((BasicDBObject) basicDBObject.get("name")).get("$in"));

        assertEquals("RegEx1.*", ((Pattern) parsedArrayListValues.get(0)).pattern());
        assertEquals(2, ((Pattern) parsedArrayListValues.get(0)).flags());
        assertEquals("regex2.*", ((Pattern) parsedArrayListValues.get(1)).pattern());
        assertEquals(8, ((Pattern) parsedArrayListValues.get(1)).flags());
    }

    /**
     * query parser test for parsing complex query containing "or" operator as key and the value of or operator
     * is object which is key-value pair of a field and regex object of format /<regex string>/
     */
    @Test
    public void parseRegexObjArrayWithOROperator() {
        JSONParser jsonParser = new JSONParser(complexQueryArrayOfRegexObjectWithORKey);
        BasicDBObject basicDBObject = (BasicDBObject) jsonParser.parse();

        BasicDBList parsedArrayListValues = (BasicDBList) basicDBObject.get("$or");

        assertEquals("RegEx1\n.*", ((Pattern) (((BasicDBObject) parsedArrayListValues.get(0)).get("name"))).pattern());
        assertEquals(2, ((Pattern) (((BasicDBObject) parsedArrayListValues.get(0)).get("name"))).flags());
        assertEquals("regex2\n.*", ((Pattern) (((BasicDBObject) parsedArrayListValues.get(1)).get("name"))).pattern());
        assertEquals(8, ((Pattern) (((BasicDBObject) parsedArrayListValues.get(1)).get("name"))).flags());
    }


}
