## EXAMPLE

@prefix rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs:	    <http://www.w3.org/2000/01/rdf-schema#> .
@prefix xsd:        <http://www.w3.org/2001/XMLSchema#> .
@prefix lm:         <http://jena.hpl.hp.com/2004/08/location-mapping#> .

# Application location to alternative location mappings.
#
# + Order does not matter.
# + The location mapping parser looks for lm:mapping properties
#   and uses the object value so this can be written in several different styles.
# 
# The translation algorithm is:
#
# 1 - Exact mappings: these are tried before attempting a prefix match.
# 2 - By prefix: find the longest matching prefix
# 3 - Use the original if no alternative.

# Use N3's , (multiple objects => multiple statements of same subject and predicate)
# Note the commas

## -- Example 1

[] lm:mapping
   [ lm:name "file:foo.n3" ; lm:altName "file:etc/foo.n3" ] ,
   [ lm:prefix "file:etc/" ; lm:altPrefix "file:ETC/" ] ,
   [ lm:name "file:etc/foo.n3" ; lm:altName "file:DIR/foo.n3" ] ,
   .

## -- Example 2

# This is exactly the same graph using the ; syntax of N3
# Multiple statements with the same subject - and we used the same predicate.

## []	lm:mapping [ lm:name "file:foo.n3" ; lm:altName "file:etc/foo.n3" ] ;
## 	lm:mapping [ lm:prefix "file:etc/" ; lm:altPrefix "file:ETC/" ] ;
## 	lm:mapping [ lm:name "file:etc/foo.n3" ; lm:altName "file:DIR/foo.n3" ] ;
## 	.

## -- Example 3

# Different graph - same effect.  The fact there are different subjects is immaterial.

## []	lm:mapping [ lm:name "file:foo.n3" ; lm:altName "file:etc/foo.n3" ] .
## []	lm:mapping [ lm:prefix "file:etc/" ; lm:altPrefix "file:ETC/" ] .
## []	lm:mapping [ lm:name "file:etc/foo.n3" ; lm:altName "file:DIR/foo.n3" ] .
