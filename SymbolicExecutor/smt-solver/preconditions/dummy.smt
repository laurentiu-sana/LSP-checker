(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrasorts (TYPE1 TYPE2)
  :extrafuns ( (x Int) )
  :formula
  ; Superclass: x < 0
  ; Subclass: x < 10
  (not (or (implies (not ( < x 0)) (not ( < x 10)) )))
)

