# MWG Storage Protocol

MWG uses Base64-like compressed string representation to save/load the various state chunks to permanent storages.
Hereafter are the details of these strings protocol.

# 1. StateChunk:


    StateChunk := Size ( '|' Type '|' Key '|' Value )
    Size := Long
    Type := Int
    Key := Long
    Value := ( PrimitiveValue | Matrix | Relation | Map | EGraph )
    PrimitiveValue := (Bool | Int | Long | Double | String)
    
    Relation := Size ( ':' NodeID )*
    RelationIndexed := Size ( ':' Long ':' Long )*
    NodeID := Long


 ## 1.1 Matrix
 
 
    Matrix := (Matrix | LMatrix)
    Matrix := Size ( ':' Double )
    LMatrix := Size ( ':' Long )
    
    
 ## 1.2 Maps    

    
    Map := ( LongToLongMap | LongToLongArrayMap | StringToLongMap )
    LongToLongMap := Size ( ':' Long ':' Long )*
    LongToLongArrayMap := Size ( ':' Long ':' Long )*
    StringToLongMap := Size ( ':' Long ':' Long )*
 
    
## 1.3 EGraph


    EGraph := Size ( '$' ENode )
    ENode := Size ( '|' Type '|' Key '|' Value )
    EValue := PrimitiveValue | Matrix | Relation | Map | ENodeRef | ERelation
    ENodeRef := Int
    ERelation := Size ( ':' Int )
   
   
# 2. TimeTree Chunk

    TimeTree := Size ( '|' Timestamp )*
    Size := Long
    Timestamp := Long

# 3. WorldOrder Chunk

    WorldOrder := ( Extra '|' )? Size ( ':' WorldID ':' DivergenceTimePoint )* 
    Extra := Long
    Size := Long
    WorldID := Long
    DivergenceTimePoint := Long
