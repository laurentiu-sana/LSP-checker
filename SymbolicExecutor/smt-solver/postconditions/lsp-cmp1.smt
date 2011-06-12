(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (xsub Int) )
  :formula
    (not
     (and
	  (exists (xsuper Int)
	   (and (< xsuper 0) (< xsub 100) (= xsuper (* 3 xsub)))
      )
     )
    )
)