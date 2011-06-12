(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (xsub Int) )
  :formula
    ; Superclass
    ;   if (x <= 0) return -x;
    ;   else return x;
    
    ; Subclass
    ;   if (x <= 10) return -x + 9;
    ;   else return x - 1;
    
    ; super(x) >= 0 \forall x,
    ; but sub(x) = -1 for x = 10 and violates the LSP

    (not
     (exists (xsuper Int)
      (or
	   (and (<= xsuper 0) (<= xsub 10) (= (- 0 xsuper) (+ (- 0 xsub) 9)))
	   (and (<= xsuper 0) (> xsub 10) (= (- 0 xsuper) (- xsub 1)))
	   (and (> xsuper 0) (<= xsub 10) (= xsuper (+ (- 0 xsub) 9)))
	   (and (> xsuper 0) (> xsub 10) (= xsuper (- xsub 1)))
	  )
	 )
	)
)
