(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (x Int) )
  :formula
    (not 
     (and

      (implies
       (and (true) (true))
       (= x x)
      )

     )
    )
