(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (x Int) )
  :formula
  ; Subclass: x < 0
  ; Superclass: x < 10
  (not (implies (not (< x 0)) (not (< x 10))))
)

