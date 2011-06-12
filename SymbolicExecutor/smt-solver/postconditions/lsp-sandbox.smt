(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (xsub Int) (ysub Int) )
  :formula
(not (or  (exists ( xsuper Int )  (and  ( < xsuper 0 )   ( < xsub 0 )   (=  ( - 0 xsuper )   ( - ( - 0 xsub ) 1 )  ) ) ) (exists ( xsuper Int )  (and  ( < xsuper 0 )   ( >= xsub 0 )   (=  ( - 0 xsuper )   ( - xsub 1 )  ) ) ) (exists ( xsuper Int )  (and  ( >= xsuper 0 )   ( < xsub 0 )   (=  xsuper   ( - ( - 0 xsub ) 1 )  ) ) ) (exists ( xsuper Int )  (and  ( >= xsuper 0 )   ( >= xsub 0 )   (=  xsuper   ( - xsub 1 )  ) ) )) )

)
