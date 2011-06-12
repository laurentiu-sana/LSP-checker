(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (x Int) )
  :formula 
    ; Superclass: x < 0
    ; Subclass: x < -1
    (not (implies (not (< x 0)) (not (< x -1))))
)

