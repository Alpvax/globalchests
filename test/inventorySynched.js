function Inventory(key)
{
    this.key = key;
    this.invRef = firebase.database().ref('/inventory/' + key);
    this.name = this.toString();
    this.size = 27;//Default
    this.items = {};
    inv = this;
    invRef.child("name").on("value", function(snap)
    {
        inv.name = snap.val();
    });
    invRef.child("size").on("value", function(snap)
    {
        inv.size = snap.val();
    });
    invRef.child("items").on("child_added", function(snap)
    {
        inv.items[snap.key] = snap.val();//TODO:Convert to ItemStack
    });
    invRef.child("items").on("child_removed", function(snap)
    {
        delete inv.items[snap.key];
    });
    function validateSlotIndex(slot)
    {
        i = parseInt(slot);
        return i < inv.size ? i : -1;
    }
    this.setStackInSlot = function(slot, stack)
    {
        index = validateSlotIndex(slot);
        if(index >= 0)
        {
            var slotRef = invRef.child("items/" + index);
            if(stack)
            {
                slotRef.set(stack);
            }
            else
            {
                slotRef.remove();
            }
        }
    };
    this.getItem = function(slot)
    {
        index = validateSlotIndex(slot);
        if(index >= 0)
        {
            return items[index];
        }
    };
}