(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (xsub Int) (ysub Int) )
  :formula
    (not
     (or
      (and
	   (exists (xsuper Int)
	    (exists (ysuper Int)
         (and ( > ( - xsuper ysuper) 0) (and ( > ysub 0) (and ( < xsub 0) (and ( < xsub 0) ( > ( + ysub xsub) 0)))) (= ysuper (* 2 xsub)))
        )
       )
      )
     )
    )
)
