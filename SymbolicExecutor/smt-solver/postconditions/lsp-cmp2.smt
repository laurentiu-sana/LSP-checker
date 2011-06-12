(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (xsub Int) )
  :formula
    ; Superclass
    ;   if (x < 0) return -x;
    ;   else return x;
    
    ; Subclass
    ;   if (x < 10) return -x + 10;
    ;   else return x;
    
    (not
     (exists (xsuper Int)
      (or
	   (and (< xsuper 0) (< xsub 10) (= (- 0 xsuper) (+ (- 0 xsub) 10)))
	   (and (< xsuper 0) (>= xsub 10) (= (- 0 xsuper) xsub))
	   (and (>= xsuper 0) (< xsub 10) (= xsuper (+ (- 0 xsub) 10)))
	   (and (>= xsuper 0) (>= xsub 10) (= xsuper xsub))
	  )
	 )
	)
)
