ALTER TABLE "person" ADD COLUMN "_case_id__suspect" uuid NOT NULL REFERENCES "case"("id");
ALTER TABLE "evidence" ADD COLUMN "found_by" uuid NOT NULL REFERENCES "person"("id");
ALTER TABLE "case" ADD COLUMN "investigator" uuid NULL REFERENCES "person"("id");
ALTER TABLE "evidence" ADD COLUMN "_case_id__evidence" uuid NOT NULL REFERENCES "case"("id");