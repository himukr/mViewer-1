package com.imaginea.mongodb.utils;

import com.mongodb.BasicDBObject;
import static junit.framework.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by himanshuk on 23/6/15.
 */
public class JSONParserTest {

    static String queryWithOneKeyAndStringValue,
                  queryWithKeyValueAsObject,
                  queryWithKeyAndRegexValue,
            queryWithKeyAndValueUsingRegexOpertor;
    @BeforeClass
    public static void setUpQueries(){
        queryWithOneKeyAndStringValue="{\n  name     :    \"basic query\"}";
        queryWithKeyValueAsObject="{name:\"query Contain object\" ,  age:{$gt:35,$lt:40}}";
        queryWithKeyAndRegexValue="{name:/Ba s*./i}";
        queryWithKeyAndValueUsingRegexOpertor="{name:{$regex:\"Ba s*.\",$options:\"ix\"}}";
    }

    @Test
    public void parseStringTest(){
        JSONParser jsonParser=new JSONParser(queryWithOneKeyAndStringValue);
        try{
            BasicDBObject basicDBObject= (BasicDBObject) jsonParser.parse();
            String parsedString=(String)basicDBObject.get("name");
            assertEquals("basic query",parsedString);
        }catch(Exception e){
            fail("parser failed to parse");
        }
    }

    @Test
    public void parseObjectTest(){
        JSONParser jsonParser=new JSONParser(queryWithKeyValueAsObject);
        BasicDBObject basicDBObject= (BasicDBObject)jsonParser.parse();
        BasicDBObject parsedAgeObject=(BasicDBObject)basicDBObject.get("age");

        int parsedAgeKeyGT=(Integer)parsedAgeObject.get("$gt");
        int parsedAgeKeyLT=(Integer)parsedAgeObject.get("$lt");

        assertEquals(35,parsedAgeKeyGT);
        assertEquals(40,parsedAgeKeyLT);
    }

    @Test
    public void parseRegexPatternObjTest(){
        JSONParser jsonParser=new JSONParser(queryWithKeyAndRegexValue);
        BasicDBObject basicDBObject= (BasicDBObject)jsonParser.parse();
        Pattern parsedRegexPattern=(Pattern)basicDBObject.get("name");



    }

}
