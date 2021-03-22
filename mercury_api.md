
**Mercury NLP APIs**
----
invoke

* **Description**

  Returns JSON data about the entities and relations in a paragraph of text processed by a specified pipeline.

* **HOST**

  http://www.autowire-code.com  

* **URL**

  /api/nlp

* **Method:**

  `POST`
  
* **URL Params**

  None

* **Header Params**

  **Required**

  `x-api-key: Bearer < your key >` <br>
  `Content-Type: application/json`

* **Data Params**

  **Required:**

  `{"pipeline":<pipeline tag>,"text":<text>}`

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    ```
    { 
      "bratFile": {"text":"", "entities":[], "relations":[]},
      "bratSem": {},
      "tokenBratFile": "",
      "tokenBratSem": "",
      "sentBratFile":{},
      "typeDefXml":{
        "entity_types":[],
        "relation_types":[]
      },
      "outputTxt": null,
      "pieCharts": null,
      "preannotation": true,
      "hasSuggestion": false,
      "createdTime": null,
    }
    ```
 
* **Error Response:**

  * **Code:** 400 NOT FOUND <br />
    **Content:** `{ error : "" }`

* **Sample Call:**

  ```bash
  curl --location --request POST 'http://www.autowire-code.com/api/nlp' --header 'x-api-key: Bearer eyJ0eXAiOiJjikQiLCJhbGciOiJIUI1NiJ9.eyJ1c2VySWQiOiI1IiwidXJsjoiaHR0cDjOuH3d3dy5hdXRvd2lyZS1jb2RlLmNvbSJ9.lgNDASH4jip7jwuZVllNqiW1ZpSYdWuaOP92cmMlIi8' --header 'Content-Type: application/json' --data-raw '{"pipeline":"default_pipeline_1:$LATEST","text":"PPPPP is a 21 yo female with past psychiatric history 
  significant for SIMD , opiate use disorder , benzodiazepine use disorder , amphetamine use disorder , 
  and generalized anxiety disorder who presents with SI with plan to overdose on heroin in the context 
  of heroin , cocaine , methamphetamine abuse , medication noncompliance and psychosocial stressors ."}'
  ```

* **Notes:**

  None